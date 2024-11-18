from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from vertexai.generative_models import GenerationConfig, GenerativeModel, Image
import http.client
import typing
import urllib.request
import os
import json
import re
# from PIL import Image as PIL_Image
# from PIL import ImageOps as PIL_ImageOps


# Initialize APIRouter
router = APIRouter(
    prefix="/extract-tag",
    tags=["Generate"],
    responses={404: {"description": "Not found"}},
)

# Configuration
GOOGLE_CREDENTIALS_PATH = "C:/Users/SSAFY/Desktop/d209me/testcahtbot-f58ecbe6e49d.json"
PROJECT_ID = "testcahtbot"
LOCATION = "asia-northeast3"
MODEL_NAME = "gemini-1.5-flash-002"

# Set Google Cloud credentials
os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = GOOGLE_CREDENTIALS_PATH

# Request Model
class ImageRequest(BaseModel):
    image_url: str

def get_image_bytes_from_url(image_url: str) -> bytes:
    try:
        with urllib.request.urlopen(image_url) as response:
            response = typing.cast(http.client.HTTPResponse, response)
            image_bytes = response.read()
        return image_bytes
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Failed to fetch image from URL: {str(e)}")


def load_image_from_url(image_url: str) -> Image:
    image_bytes = get_image_bytes_from_url(image_url)
    return Image.from_bytes(image_bytes)

async def generate_analysis(image: Image):
    try:
        # Initialize model
        # model = GenerativeModel("gemini-1.0-pro-vision")
        model = GenerativeModel(MODEL_NAME)
        
        # Generation configuration
        generation_config = GenerationConfig(
            max_output_tokens=256,
            temperature=0.9,
            top_p=0.95,
        )
        
        # Prompt configuration
        prompt = """다음 JSON 형식으로만 응답해주세요.:
        {
            "colors": [주요 색상 최대 2개(한글)],
            "mood": [분위기 최대 2개(한글)],
            "category": "카테고리 1개(한글)",
            "text": "이미지 속 텍스트(한글). 없으면 null "
            "suggested":"이미지를 보고 태그 제안 1개(한글)"
        }"""
        
        # Generate content
        responses = model.generate_content(
            [image, prompt],
            generation_config=generation_config,
            stream=True,
        )
        
        # Process response
        full_response = ""
        for response in responses:
            if response.text:
                full_response += response.text
        
        # Extract JSON
        json_match = re.search(r'{.*}', full_response, re.DOTALL)
        if json_match:
            json_str = json_match.group(0)
            json_response = json.loads(json_str)
            return {"status": "success", "data": json_response}
        else:
            return {"status": "error", "message": "JSON 추출 실패", "raw_response": full_response}
                
    except Exception as e:
        return {"status": "error", "message": str(e)}

# Endpoint to analyze image tags
@router.post("/", summary="Analyze image tags")
async def analyze_image(request: ImageRequest):
    image = load_image_from_url(request.image_url)
    result = await generate_analysis(image)
    if result["status"] == "success":
        return result["data"]
    else:
        raise HTTPException(status_code=500, detail=result["message"])
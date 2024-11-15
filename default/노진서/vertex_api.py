# vertex_api.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import vertexai
from vertexai.preview.generative_models import GenerativeModel
import os
import http.client
import typing
import urllib.request
from PIL import Image as PIL_Image
from PIL import ImageOps as PIL_ImageOps
import IPython.display

# 환경 변수 설정
os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = "C:/Users/SSAFY/Desktop/d209-me/testcahtbot-f58ecbe6e49d.json"
projectId = "testcahtbot"

# Vertex AI 초기화
vertexai.init(project=projectId, location="asia-northeast3")
app = FastAPI()

# 모델 초기화
multimodal_model = GenerativeModel("gemini-1.5-flash-002")

# 데이터 모델 정의
class ImageRequest(BaseModel):
    image_url: str

# 이미지 로드 함수
def get_image_bytes_from_url(image_url: str) -> bytes:
    with urllib.request.urlopen(image_url) as response:
        response = typing.cast(http.client.HTTPResponse, response)
        image_bytes = response.read()
    return image_bytes

def load_image_from_url(image_url: str) -> PIL_Image.Image:
    image_bytes = get_image_bytes_from_url(image_url)
    pil_image = PIL_Image.open(io.BytesIO(image_bytes))
    if pil_image.mode != "RGB":
        pil_image = pil_image.convert("RGB")
    return pil_image

# FastAPI 엔드포인트 정의
@app.post("/generate-content/")
async def generate_content(request: ImageRequest):
    # 이미지 URL로부터 PIL 이미지 로드
    try:
        image = load_image_from_url(request.image_url)
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"이미지를 로드할 수 없습니다: {str(e)}")

    # Prompt 설정
    prompt = '{"colors": 주된 이미지 색상 2개 이하, "mood": 분위기 2개 이하, "text": 텍스트} json 추출'
    contents = [image, prompt]

    # 모델을 사용하여 콘텐츠 생성
    try:
        responses = multimodal_model.generate_content(contents=contents, stream=True)
        result = "".join([response.text for response in responses])
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"콘텐츠 생성 중 오류 발생: {str(e)}")

    # 결과 반환
    return {"result": result}
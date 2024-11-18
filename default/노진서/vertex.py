
# vertex_api_test
from vertexai.preview.generative_models import GenerativeModel
import vertexai
# vertexai.init(project=PROJECT_ID, location=LOCATION)
from vertexai.generative_models import GenerationConfig, GenerativeModel, Image, Part
import os

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = \
"C:/Users/SSAFY/Desktop/d209-me/testcahtbot-f58ecbe6e49d.json"
projectId = "testcahtbot"
# vertexai.init(project=projectId, location="asia-northeast3")

# user_message = "인공지능에 대해 한 문장으로 말하세요."
# model = GenerativeModel(model_name='gemini-pro')    
# resp = model.generate_content(user_message)
# print(resp.text)

##################################################
# vertex_api_test.py
# multimodal_model = GenerativeModel("gemini-1.0-pro-vision")
multimodal_model = GenerativeModel("gemini-1.5-flash-002")

import http.client
import typing
import urllib.request

import IPython.display
from PIL import Image as PIL_Image
from PIL import ImageOps as PIL_ImageOps
# from vertexai.preview.generative_models import Image

def display_images(
    images: typing.Iterable[Image],
    max_width: int = 600,
    max_height: int = 350,
) -> None:
    for image in images:
        pil_image = typing.cast(PIL_Image.Image, image._pil_image)
        if pil_image.mode != "RGB":
            # RGB is supported by all Jupyter environments (e.g. RGBA is not yet)
            pil_image = pil_image.convert("RGB")
        image_width, image_height = pil_image.size
        if max_width < image_width or max_height < image_height:
            # Resize to display a smaller notebook image
            pil_image = PIL_ImageOps.contain(pil_image, (max_width, max_height))
        IPython.display.display(pil_image)


def get_image_bytes_from_url(image_url: str) -> bytes:
    with urllib.request.urlopen(image_url) as response:
        response = typing.cast(http.client.HTTPResponse, response)
        image_bytes = response.read()
    return image_bytes


def load_image_from_url(image_url: str) -> Image:
    image_bytes = get_image_bytes_from_url(image_url)
    return Image.from_bytes(image_bytes)


def get_url_from_gcs(gcs_uri: str) -> str:
    # converts gcs uri to url for image display.
    url = "https://storage.googleapis.com/" + gcs_uri.replace("gs://", "").replace(
        " ", "%20"
    )
    return url


def print_multimodal_prompt(contents: list):
    """
    Given contents that would be sent to Gemini,
    output the full multimodal prompt for ease of readability.
    """
    for content in contents:
        if isinstance(content, Image):
            display_images([content])
        elif isinstance(content, Part):
            url = get_url_from_gcs(content.file_data.file_uri)
            IPython.display.display(load_image_from_url(url))
        else:
            print(content)


# Load image from Cloud Storage URI
image_url = (
    "https://postfiles.pstatic.net/MjAyNDExMDdfNSAg/MDAxNzMwOTU1MzUyMTAz.1mubCpVfTI7NyJKdB-6BfVG1vh5f4kKqF8Nr6ilVeCIg.sa9KIrOWvtwSqSbNVrEbLQyfjTm9KOG6V8FS0O7l-Z8g.JPEG/A1.jpg?type=w773"
)
image = load_image_from_url(image_url)  # convert to bytes

# Prepare contents
prompt = '{"colors":주된 이미지 색상 2개 이하,"mood": 분위기 2개 이하, "text": 텍스트} json 추출'
contents = [image, prompt]

responses = multimodal_model.generate_content(contents, stream=True)

print("-------Prompt--------")
print_multimodal_prompt(contents)

print("\n-------Response--------")
for response in responses:
    print(response.text, end="")
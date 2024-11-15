# main.py
from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import StreamingResponse
from rembg import remove
from PIL import Image
import io

app = FastAPI()

@app.get("/")
async def read_root():
    return {"message": "Hello, FastAPI!"}




@app.post("/remove-background/")
async def remove_background(file: UploadFile = File(...)):
    try:
        # 파일을 PIL 이미지로 열기
        input_image = Image.open(file.file)
        
        # 배경 제거
        output_image = remove(input_image)
        
        # 결과 이미지를 바이트 버퍼에 저장
        buffer = io.BytesIO()
        output_image.save(buffer, format="PNG")
        buffer.seek(0)
        
        # 클라이언트에 이미지 반환
        return StreamingResponse(buffer, media_type="image/png")



    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


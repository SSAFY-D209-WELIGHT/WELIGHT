# main.py
from fastapi import FastAPI

app = FastAPI(
    title="My API",  # Swagger UI의 제목 설정
    description="This is a sample API with Docker and Nginx setup.",  # API 설명 추가
    version="1.0.0",  # API 버전
    root_path="/ai",  # Nginx 프록시 설정과 일치
    docs_url="/api-docs",  # Swagger UI 경로
    redoc_url=None  # ReDoc 비활성화 (필요 시 설정)
)

@app.get("/")
async def root():
    return {"message": "Hello, FastAPI with Docker!"}

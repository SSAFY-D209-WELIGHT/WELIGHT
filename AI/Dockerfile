# 1. 베이스 이미지로 Python 3.9 사용 (FastAPI 및 rembg 호환)
FROM python:3.9

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 필요한 패키지 설치
COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

# 4. 애플리케이션 파일 복사
COPY ./main.py .

# 5. FastAPI 실행을 위한 Uvicorn 설치 및 포트 설정
EXPOSE 8000

# 6. 컨테이너 실행 시 Uvicorn으로 FastAPI 앱 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]

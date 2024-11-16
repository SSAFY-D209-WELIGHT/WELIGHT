# Dockerfile
FROM python:3.11-slim

WORKDIR /app

# 시스템 의존성 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    pkg-config \
    libmariadb-dev-compat \
    libmariadb-dev \
    libssl-dev \
    libffi-dev \
    libjpeg-dev \
    zlib1g-dev \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender-dev \
    && rm -rf /var/lib/apt/lists/*

# requirements.txt 먼저 복사하여 캐시 활용
COPY requirements.txt ./

# pip 업그레이드 및 Python 패키지 설치
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# requirements.txt에 명시된 모든 패키지 설치
# RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 코드 복사
COPY . /app

# 컨테이너 실행 시 실행할 명령어 설정
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]

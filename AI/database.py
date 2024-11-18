# database.py
from dotenv import load_dotenv
import os
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from urllib.parse import quote_plus

# Load environment variables from .env
load_dotenv()

# Database 설정
DB_USER = "superuser"
DB_PASSWORD = os.getenv('DB_PASSWORD')  
DB_HOST = "3.34.189.155"
DB_PORT = "3306"
DB_NAME = "WELIGHT"

# URL 생성
DATABASE_URL = f"mysql+pymysql://{DB_USER}:{quote_plus(DB_PASSWORD)}@{DB_HOST}:{DB_PORT}/{DB_NAME}?charset=utf8mb4"
# &timezone=Asia/Seoul

# Engine 설정
engine = create_engine(
    DATABASE_URL,
    pool_size=5,  # 커넥션 풀 크기
    max_overflow=10,  # 최대 초과 커넥션
    pool_timeout=30,  # 커넥션 타임아웃 (초)
    pool_recycle=3600,  # 커넥션 재사용 시간 (1시간)
    echo=False  # SQL 로그 출력 여부
)

# 세션 설정
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Base 클래스 생성
Base = declarative_base()

# DB 연결 Generator
def get_db():
    """
    데이터베이스 세션 생성
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# 데이터베이스 연결 테스트 함수
def test_connection():
    """
    데이터베이스 연결 테스트
    """
    try:
        db = SessionLocal()
        db.execute("SELECT 1")
        print("데이터베이스 연결 성공!")
        return True
    except Exception as e:
        print(f"데이터베이스 연결 실패: {str(e)}")
        return False
    finally:
        db.close()

if __name__ == "__main__":
    # 연결 테스트 실행
    test_connection()

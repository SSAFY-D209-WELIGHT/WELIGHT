# models.py
from sqlalchemy import Column, Integer, String, DateTime, Boolean, BIGINT, ForeignKey, CHAR, Float
from database import Base
from datetime import datetime
from pydantic import BaseModel
from typing import Optional, Dict, Any
from sqlalchemy.orm import relationship


"""
DisplayBase는 요청 데이터의 기본 스키마로 STORE_ID, DISPLAY_PW, DISPLAY_NICKNAME, IMAGE_URL, IS_ACTIVE 필드를 포함합니다.
DisplayCreate는 DisplayBase를 상속하여, 새 레코드를 생성할 때 사용할 수 있는 요청 스키마입니다.
DisplayResponse는 응답 스키마로, 데이터베이스에서 가져온 데이터를 API 응답으로 반환할 때 사용되며, 
DISPLAY_UID, CREATOR_UID, DISPLAY_NAME, DISPLAY_THUMBNAIL_URL 등의 필드를 포함합니다.
"""
class Display(Base):
    __tablename__ = "DISPLAY"

    DISPLAY_UID = Column(BIGINT, primary_key=True, index=True)
    CREATOR_UID = Column(BIGINT)
    DISPLAY_NAME = Column(String(100))
    DISPLAY_THUMBNAIL_URL = Column(String(255), nullable=True)
    DISPLAY_IS_POSTED = Column(Boolean, default=True)
    DISPLAY_CREATED_AT = Column(DateTime, default=datetime.utcnow)
    DISPLAY_DOWNLOAD_COUNT = Column(BIGINT, default=0)
    DISPLAY_LIKE_COUNT = Column(BIGINT, default=0)
    DISPLAY_VIEW_COUNT = Column(BIGINT, default=0)

    images = relationship("DisplayImage", back_populates="display")

# Pydantic 모델 (요청/응답 스키마)
class DisplayBase(BaseModel):
    STORE_ID: str
    DISPLAY_PW: str
    DISPLAY_NICKNAME: str
    IMAGE_URL: Optional[str] = None
    IS_ACTIVE: Optional[bool] = True

class DisplayCreate(DisplayBase):
    pass

class DisplayResponse(BaseModel):
    DISPLAY_UID: int
    CREATOR_UID: Optional[int] = None
    DISPLAY_NAME: Optional[str] = None
    DISPLAY_THUMBNAIL_URL: Optional[str] = None
    DISPLAY_IS_POSTED: Optional[bool] = None
    DISPLAY_CREATED_AT: Optional[datetime] = None
    DISPLAY_DOWNLOAD_COUNT: Optional[int] = None
    DISPLAY_LIKE_COUNT: Optional[int] = None
    DISPLAY_VIEW_COUNT: Optional[int] = None

    class Config:
        from_attributes = True
        # orm_mode = True

"""
DisplayTag는 DISPLAY_TAG 테이블과 매핑되는 SQLAlchemy 모델입니다.
DISPLAY_TAG_UID: 태그의 고유 식별자 (기본키).
DISPLAY_UID: 태그가 연결된 디스플레이의 ID.
DISPLAY_TAG_TEXT: 태그의 텍스트.
DISPLAY_TAG_CREATED_AT: 태그 생성 시간 (CURRENT_TIMESTAMP 기본값).
DisplayTagBase는 Pydantic 모델로, 기본 태그 정보를 포함합니다.
DisplayTagCreate는 태그 생성 요청에 사용할 수 있는 Pydantic 모델입니다.
DisplayTagResponse는 태그 정보를 응답할 때 사용할 Pydantic 모델입니다. 
DISPLAY_TAG_UID와 DISPLAY_TAG_CREATED_AT를 포함하며, ORM 모드가 활성화되어 SQLAlchemy 객체를 직접 반환할 수 있습니다.
"""

class DisplayTag(Base):
    __tablename__ = "DISPLAY_TAG"

    DISPLAY_TAG_UID = Column(BIGINT, primary_key=True, index=True)
    DISPLAY_UID = Column(BIGINT, nullable=False)
    DISPLAY_TAG_TEXT = Column(String(50), nullable=False)
    DISPLAY_TAG_CREATED_AT = Column(DateTime, default=datetime.utcnow)

# Pydantic 모델 (요청/응답 스키마)
class DisplayTagBase(BaseModel):
    DISPLAY_UID: int
    DISPLAY_TAG_TEXT: str

# DisplayTag 생성 요청용 Pydantic 모델
class DisplayTagCreate(BaseModel):
    DISPLAY_UID: int
    DISPLAY_TAG_TEXT: str

    class Config:
        from_attributes = True
        # orm_mode = True


# DisplayTag 응답용 Pydantic 모델
class DisplayTagResponse(BaseModel):
    DISPLAY_TAG_UID: int
    DISPLAY_UID: int
    DISPLAY_TAG_TEXT: str
    DISPLAY_TAG_CREATED_AT: Optional[datetime]

    class Config:
        from_attributes = True
        # orm_mode = True

### DISPLAY_IMAGE
class DisplayImage(Base):
    __tablename__ = "DISPLAY_IMAGE"

    DISPLAY_IMG_UID = Column(BIGINT, primary_key=True, index=True)
    DISPLAY_UID = Column(BIGINT, ForeignKey("DISPLAY.DISPLAY_UID"))
    DISPLAY_IMG_URL = Column(String(255))
    DISPLAY_IMG_COLOR = Column(CHAR(9))
    DISPLAY_IMG_SCALE = Column(Float)
    DISPLAY_IMG_ROTATION = Column(Float)
    DISPLAY_IMG_OFFSETX = Column(Float)
    DISPLAY_IMG_OFFSETY = Column(Float)
    DISPLAY_IMG_CREATED_AT = Column(DateTime, default=datetime.utcnow)

    # Relationship with Display (if you want to use it)
    display = relationship("Display", back_populates="images")

class DisplayImageBase(BaseModel):
    DISPLAY_UID: int
    DISPLAY_IMG_URL: Optional[str] = None
    DISPLAY_IMG_COLOR: Optional[str] = None
    DISPLAY_IMG_SCALE: Optional[float] = None
    DISPLAY_IMG_ROTATION: Optional[float] = None
    DISPLAY_IMG_OFFSETX: Optional[float] = None
    DISPLAY_IMG_OFFSETY: Optional[float] = None
    
class DisplayImageCreate(DisplayImageBase):
    pass

class DisplayImageResponse(DisplayImageBase):
    DISPLAY_IMG_UID: int
    DISPLAY_IMG_CREATED_AT: datetime

    class Config:
        orm_mode = True



### Display_Tag
class TagCheckResponse(BaseModel):
    image_id: int
    display_uid: int
    tag_count: int

    class Config:
        orm_mode = True #SQLAlchemy 모델과 Pydantic 모델 간의 호환성을 유지

class TagCheckResponseWithAnalysis(TagCheckResponse):
    analysis: Optional[Dict[str, Any]] = None

    class Config:
        orm_mode = True
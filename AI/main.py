# main.py
from fastapi import FastAPI, File, UploadFile, HTTPException, Depends
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from typing import Dict, Any, List
## REMOVE BACKGROUND IMAGE
from rembg import remove 
## VERTEX AI
from PIL import Image as PIL_Image
from PIL import ImageOps as PIL_ImageOps

import os
import http.client
import typing
import urllib.request
import io
import json


## DB
# from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
import models
from database import get_db
from datetime import datetime

## extractTag.py
from extractTag import router as generate_router # 라우터 임포트
from extractTag import generate_analysis, load_image_from_url
# FastAPI app initialization
app = FastAPI(
    title="Image Processing API",
    description="API for background removal and image analysis using Vertex AI",
    version="1.0.0"
)

# # CORS 설정 (필요한 경우)
# from fastapi.middleware.cors import CORSMiddleware

# origins = [
#     "http://localhost",
#     "http://localhost:3000",  # 예: React 앱
#     # 다른 허용할 도메인 추가
# ]

# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=origins,  # 허용할 도메인 리스트
#     allow_credentials=True,
#     allow_methods=["*"],
#     allow_headers=["*"],
# )



# 라우터 포함
app.include_router(generate_router)



# API endpoints
@app.get("/")
async def read_root():
    """Root endpoint to verify API status."""
    return {
        "hello": "fastAPI",
        "status": "online",
        "message": "Image Processing API is running",
        "version": "1.0.0"
    }

@app.get("/displays/", response_model=List[models.DisplayResponse])
def read_displays(
    skip: int = 0, 
    limit: int = 10,
    is_active: bool = None,
    db: Session = Depends(get_db)
):
    """
    디스플레이 목록을 조회합니다.
    - skip: 건너뛸 레코드 수
    - limit: 조회할 최대 레코드 수
    - is_active: 활성화 상태 필터 (선택)
    """
    query = db.query(models.Display)
    if is_active is not None:
        query = query.filter(models.Display.IS_ACTIVE == is_active)
    displays = query.offset(skip).limit(limit).all()
    return displays

@app.post("/displays/", response_model=models.DisplayResponse)
def create_display(
    display: models.DisplayCreate,
    db: Session = Depends(get_db)
):
    """
    새로운 디스플레이를 생성합니다.
    """
    db_display = models.Display(**display.dict())
    db.add(db_display)
    db.commit()
    db.refresh(db_display)
    return db_display

@app.get("/displays/{display_uid}", response_model=models.DisplayResponse)
def read_display(
    display_uid: int,
    db: Session = Depends(get_db)
):
    """
    특정 디스플레이의 상세 정보를 조회합니다.
    """
    display = db.query(models.Display).filter(models.Display.DISPLAY_UID == display_uid).first()
    if display is None:
        raise HTTPException(status_code=404, detail="Display not found")
    return display

@app.put("/displays/{display_uid}", response_model=models.DisplayResponse)
def update_display(
    display_uid: int,
    display_update: models.DisplayCreate,
    db: Session = Depends(get_db)
):
    """
    특정 디스플레이의 정보를 업데이트합니다.
    """
    db_display = db.query(models.Display).filter(models.Display.DISPLAY_UID == display_uid).first()
    if db_display is None:
        raise HTTPException(status_code=404, detail="Display not found")
    
    update_data = display_update.dict(exclude_unset=True)
    for field, value in update_data.items():
        setattr(db_display, field, value)
    
    db_display.UPDATED_AT = datetime.utcnow()
    db.commit()
    db.refresh(db_display)
    return db_display

@app.delete("/displays/{display_uid}")
def delete_display(
    display_uid: int,
    db: Session = Depends(get_db)
):
    """
    특정 디스플레이를 삭제합니다.
    """
    db_display = db.query(models.Display).filter(models.Display.DISPLAY_UID == display_uid).first()
    if db_display is None:
        raise HTTPException(status_code=404, detail="Display not found")
    
    db.delete(db_display)
    db.commit()
    return {"message": f"Display {display_uid} deleted successfully"}


###TAG
# 전체 태그 목록 조회
@app.get("/tags/", response_model=List[models.DisplayTagResponse])
def read_all_display_tags(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    """
    전체 태그 목록을 조회합니다.
    - skip: 건너뛸 레코드 수
    - limit: 조회할 최대 레코드 수
    """
    tags = db.query(models.DisplayTag).offset(skip).limit(limit).all()
    return tags


### CHECK TAGS

@app.get("/generate-tags", response_model=List[models.TagCheckResponseWithAnalysis])
async def check_tags(
    skip: int = 0,
    limit: int = 100,
    db: Session = Depends(get_db)
):
    """
    DISPLAY_IMG_URL이 존재하는 모든 디스플레이 이미지에 대한 태그 개수를 확인하고,
    tag_count가 5개 미만인 경우 해당 display_image의 URL을 분석하여 태그를 추출합니다.
    """
    from sqlalchemy import func

    tag_counts = (
        db.query(
            models.DisplayImage.DISPLAY_IMG_UID.label("image_id"),
            models.DisplayImage.DISPLAY_UID.label("display_uid"),
            func.count(models.DisplayTag.DISPLAY_TAG_UID).label("tag_count")
        )
        .outerjoin(models.DisplayTag, models.DisplayImage.DISPLAY_UID == models.DisplayTag.DISPLAY_UID)
        .filter(
            models.DisplayImage.DISPLAY_IMG_URL.isnot(None),
            models.DisplayImage.DISPLAY_IMG_URL != ""
        )
        .group_by(models.DisplayImage.DISPLAY_IMG_UID, models.DisplayImage.DISPLAY_UID)
        .offset(skip)
        .limit(limit)
        .all()
    )
    
    results = []
    for tag_count_record in tag_counts:
       response_item = {
           "image_id": tag_count_record.image_id,
           "display_uid": tag_count_record.display_uid,
           "tag_count": tag_count_record.tag_count
       }

       if tag_count_record.tag_count < 5:
           display_image = db.query(models.DisplayImage).filter(
               models.DisplayImage.DISPLAY_IMG_UID == tag_count_record.image_id
           ).first()
           
           if display_image and display_image.DISPLAY_IMG_URL:
               try:
                   image = load_image_from_url(display_image.DISPLAY_IMG_URL)
                   if image:
                       analysis_result = await generate_analysis(image)
                       response_item["analysis"] = analysis_result

                       # 분석 결과가 성공적인 경우
                    #    if (analysis_result.get("status") == "success" and 
                        # 분석 결과가 성공적인 경우
                       if (analysis_result.get("status") == "success" and 
                            "data" in analysis_result):
                            
                            data = analysis_result["data"]
                            
                            # 색상 태그 생성
                            if "colors" in data:
                                for color in data["colors"]:
                                    try:
                                        db_tag = models.DisplayTag(
                                            DISPLAY_UID=display_image.DISPLAY_UID,
                                            DISPLAY_TAG_TEXT=color
                                        )
                                        db.add(db_tag)
                                    except Exception as e:
                                        print(f"Error creating tag for color {color}: {str(e)}")
                                        continue

                            # 분위기 태그 생성
                            if "mood" in data:
                                for mood in data["mood"]:
                                    try:
                                        db_tag = models.DisplayTag(
                                            DISPLAY_UID=display_image.DISPLAY_UID,
                                            DISPLAY_TAG_TEXT=mood
                                        )
                                        db.add(db_tag)
                                    except Exception as e:
                                        print(f"Error creating tag for mood {mood}: {str(e)}")
                                        continue

                            # 카테고리 태그 생성
                            if "category" in data and data["category"]:
                                try:
                                    db_tag = models.DisplayTag(
                                        DISPLAY_UID=display_image.DISPLAY_UID,
                                        DISPLAY_TAG_TEXT=data["category"]
                                    )
                                    db.add(db_tag)
                                except Exception as e:
                                    print(f"Error creating tag for category {data['category']}: {str(e)}")

                            # 추천 태그 생성
                            if "suggested" in data and data["suggested"]:
                                try:
                                    db_tag = models.DisplayTag(
                                        DISPLAY_UID=display_image.DISPLAY_UID,
                                        DISPLAY_TAG_TEXT=data["suggested"]
                                    )
                                    db.add(db_tag)
                                except Exception as e:
                                    print(f"Error creating tag for suggested {data['suggested']}: {str(e)}")

                            # 텍스트 태그 생성 (비어있지 않고 "없음"이 아닌 경우)
                            if ("text" in data and 
                                data["text"] and 
                                data["text"].strip() and 
                                "null" not in data["text"]):
                                try:
                                    db_tag = models.DisplayTag(
                                        DISPLAY_UID=display_image.DISPLAY_UID,
                                        DISPLAY_TAG_TEXT=data["text"]
                                    )
                                    db.add(db_tag)
                                except Exception as e:
                                    print(f"Error creating tag for text {data['text']}: {str(e)}")

                            # 변경사항 커밋 시도
                            try:
                                db.commit()
                            except Exception as e:
                                db.rollback()
                                print(f"Error committing changes: {str(e)}")


               except Exception as e:
                   response_item["analysis"] = {
                       "status": "error",
                       "message": f"Error analyzing image: {str(e)}"
                   }
           else:
               response_item["analysis"] = {
                   "status": "error", 
                   "message": "No image URL found"
               }
       
       results.append(response_item)
   
    return results
# 모든 display_image에 대해 태그 개수 확인 
# @app.get("/check-all-tags", response_model=List[models.TagCheckResponse])
# def check_tags(
#     skip: int = 0,
#     limit: int = 100,
#     db: Session = Depends(get_db)
# ):
#     """
#     모든 디스플레이 이미지에 대한 태그 개수를 확인    
#     - **skip**: 건너뛸 레코드 수 (기본값: 0)
#     - **limit**: 조회할 최대 레코드 수 (기본값: 100)
#     """
#     # 모든 이미지 조회
#     images = db.query(models.DisplayImage).offset(skip).limit(limit).all()
    
#     # display_uid별 태그 개수 조회를 위한 쿼리
#     from sqlalchemy import func

#     # display_uid와 해당 display_uid에 연결된 태그의 개수를 가져옵니다.
#     tag_counts = (
#         db.query(
#             models.DisplayImage.DISPLAY_IMG_UID.label("image_id"),
#             models.DisplayImage.DISPLAY_UID.label("display_uid"),
#             func.count(models.DisplayTag.DISPLAY_TAG_UID).label("tag_count")
#         )
#         .outerjoin(models.DisplayTag, models.DisplayImage.DISPLAY_UID == models.DisplayTag.DISPLAY_UID)
#         .group_by(models.DisplayImage.DISPLAY_IMG_UID, models.DisplayImage.DISPLAY_UID)
#         .offset(skip)
#         .limit(limit)
#         .all()
#     )
    
#     # 결과를 리스트로 반환
#     return tag_counts



# 디스플레이 태그 생성
@app.post("/tags/", response_model=models.DisplayTagResponse)
def create_display_tag(tag: models.DisplayTagCreate, db: Session = Depends(get_db)):
    db_tag = models.DisplayTag(
        DISPLAY_UID=tag.DISPLAY_UID,
        DISPLAY_TAG_TEXT=tag.DISPLAY_TAG_TEXT
    )
    db.add(db_tag)
    db.commit()
    db.refresh(db_tag)
    return db_tag

# 특정 디스플레이에 대한 태그 목록 조회
@app.get("/tags/{display_uid}", response_model=List[models.DisplayTagResponse])
def read_display_tags(display_uid: int, db: Session = Depends(get_db)):
    tags = db.query(models.DisplayTag).filter(models.DisplayTag.DISPLAY_UID == display_uid).all()
    if not tags:
        raise HTTPException(status_code=404, detail="Tags not found for the given display")
    return tags

# 특정 태그 업데이트
@app.put("/tags/{tag_id}", response_model=models.DisplayTagResponse)
def update_display_tag(tag_id: int, tag: models.DisplayTagCreate, db: Session = Depends(get_db)):
    db_tag = db.query(models.DisplayTag).filter(models.DisplayTag.DISPLAY_TAG_UID == tag_id).first()
    if db_tag is None:
        raise HTTPException(status_code=404, detail="Tag not found")

    db_tag.DISPLAY_UID = tag.DISPLAY_UID
    db_tag.DISPLAY_TAG_TEXT = tag.DISPLAY_TAG_TEXT
    db.commit()
    db.refresh(db_tag)
    return db_tag

# 특정 태그 삭제
@app.delete("/tags/{tag_id}", response_model=dict)
def delete_display_tag(tag_id: int, db: Session = Depends(get_db)):
    db_tag = db.query(models.DisplayTag).filter(models.DisplayTag.DISPLAY_TAG_UID == tag_id).first()
    if db_tag is None:
        raise HTTPException(status_code=404, detail="Tag not found")

    db.delete(db_tag)
    db.commit()
    return {"message": f"Tag {tag_id} deleted successfully"}


### DISPLAY_IMAGE

# Create a new image
@app.post("/images/", response_model=models.DisplayImageResponse)
def create_display_image(image: models.DisplayImageCreate, db: Session = Depends(get_db)):
    db_image = models.DisplayImage(**image.dict())
    db.add(db_image)
    db.commit()
    db.refresh(db_image)
    return db_image

### 전체 디스플레이 이미지 조회
@app.get("/images/all", response_model=List[models.DisplayImageResponse])
def read_all_display_images(
    skip: int = 0, 
    limit: int = 100, 
    db: Session = Depends(get_db)
):
    """
    모든 디스플레이 이미지 조회
    - **skip**: 건너뛸 레코드 수 (기본값: 0)
    - **limit**: 조회할 최대 레코드 수 (기본값: 100)
    """
    images = db.query(models.DisplayImage).offset(skip).limit(limit).all()
    return images

# 디스플레이 이미지 조회
@app.get("/images/{display_uid}", response_model=List[models.DisplayImageResponse])
def read_display_images(display_uid: int, db: Session = Depends(get_db)):
    images = db.query(models.DisplayImage).filter(models.DisplayImage.DISPLAY_UID == display_uid).all()
    if not images:
        raise HTTPException(status_code=404, detail="Images not found for the given display")
    return images

# Update an image
@app.put("/images/{image_id}", response_model=models.DisplayImageResponse)
def update_display_image(image_id: int, image: models.DisplayImageCreate, db: Session = Depends(get_db)):
    db_image = db.query(models.DisplayImage).filter(models.DisplayImage.DISPLAY_IMG_UID == image_id).first()
    if db_image is None:
        raise HTTPException(status_code=404, detail="Image not found")

    for field, value in image.dict(exclude_unset=True).items():
        setattr(db_image, field, value)
        
    db.commit()
    db.refresh(db_image)
    return db_image

# Delete an image
@app.delete("/images/{image_id}", response_model=dict)
def delete_display_image(image_id: int, db: Session = Depends(get_db)):
    db_image = db.query(models.DisplayImage).filter(models.DisplayImage.DISPLAY_IMG_UID == image_id).first()
    if db_image is None:
        raise HTTPException(status_code=404, detail="Image not found")

    db.delete(db_image)
    db.commit()
    return {"message": f"Image {image_id} deleted successfully"}



@app.post("/remove-background/", 
         summary="Remove background from image",
         response_class=StreamingResponse)
async def remove_background(file: UploadFile = File(...)):
    """
    Remove background from uploaded image using rembg.
    
    Args:
        file (UploadFile): Image file to process
        
    Returns:
        StreamingResponse: Processed image with background removed
    """
    try:
        input_image = Image.open(file.file)
        output_image = remove(input_image)
        
        buffer = io.BytesIO()
        output_image.save(buffer, format="PNG")
        buffer.seek(0)
        
        return StreamingResponse(
            buffer, 
            media_type="image/png",
            headers={"Content-Disposition": f"attachment; filename=processed_{file.filename}"}
        )
    except Exception as e:
        raise HTTPException(
            status_code=500, 
            detail=f"Background removal failed: {str(e)}"
        )



if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routes import classification

app = FastAPI(title="GreenGuard AI Service")

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Routes
app.include_router(classification.router, prefix="/api")

@app.get("/")
def root():
    return {"message": "GreenGuard AI Service is running"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        app, 
        host="0.0.0.0", 
        port=8000,
        timeout_keep_alive=5,  # Reduce el timeout
        log_level="info",
        access_log=True
    )
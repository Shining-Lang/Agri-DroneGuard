from fastapi import FastAPI

from app.routes.health import router as health_router


app = FastAPI(
    title="Agri UAV Agent",
    description="FastAPI gateway for AI-assisted UAV route planning.",
    version="0.1.0",
)

app.include_router(health_router, prefix="/api")

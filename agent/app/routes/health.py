from datetime import datetime, timezone

from fastapi import APIRouter

router = APIRouter(tags=["health"])


@router.get("/health")
def health() -> dict[str, str]:
    return {
        "service": "agri-uav-agent",
        "status": "UP",
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }

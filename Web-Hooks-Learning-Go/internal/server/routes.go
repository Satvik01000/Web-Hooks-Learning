package server

import (
	"net/http"

	"Web-Hooks-Learning-Go/internal/service"

	"github.com/gin-gonic/gin"
)

func (s *Server) RegisterRoutes() http.Handler {
	r := gin.Default()
	r.POST("/webhook", service.OnWebhook)

	return r
}

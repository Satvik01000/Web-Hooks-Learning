package service

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
	_ "github.com/joho/godotenv/autoload"
)

func OnWebhook(c *gin.Context) {
	githubId := c.GetHeader("X-GitHub-Hook-ID")
	githubEvent := c.GetHeader("X-GitHub-Event")
	receivedSignature := c.GetHeader("X-GitHub-Signature-256")

	fmt.Println("X-GitHub-Hook-ID:", githubId)
	fmt.Println("X-GitHub-Event:", githubEvent)

	bodyBytes, err := io.ReadAll(c.Request.Body)
	if err != nil {
		fmt.Println("Error reading body")
		c.Status(http.StatusBadRequest)
		return
	}

	secret := os.Getenv("GITHUB_WEBHOOK_SECRET")
	mac := hmac.New(sha256.New, []byte(secret))
	mac.Write(bodyBytes)
	expectedSignature := "sha256=" + hex.EncodeToString(mac.Sum(nil))

	if !hmac.Equal([]byte(receivedSignature), []byte(expectedSignature)) {
		fmt.Println("Invalid signature! Request rejected.")
		c.Status(http.StatusUnauthorized)
		return
	}

	bodyString := string(bodyBytes)
	fmt.Println("Request Body:", bodyString)

	c.Status(http.StatusAccepted)
}

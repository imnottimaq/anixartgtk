package internal

import (
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"os"
	"path/filepath"

	"github.com/adrg/xdg"
	"github.com/goccy/go-json"
	"github.com/valyala/fasthttp"
)

var apiUrl string = "https://anilibria.top" //TODO: move this into config for user to be able to change it if for example main server isnt available in their region

func GetLatestReleases() ([]Release, error) { //TODO: better error handling
	if err := ApiStatusCheck(); err != nil {
		return nil, fmt.Errorf("[GetLatestReleases] %v", err)
	}
	_, body, err := fasthttp.Get(nil, apiUrl+"/api/v1/anime/releases/latest?limit=10")
	if err != nil {
		return nil, fmt.Errorf("[GetLatestReleases] %v", err)
	}
	var releases []Release
	if err = json.Unmarshal(body, &releases); err != nil {
		return nil, fmt.Errorf("[GetLatestReleases] %v", err)
	}
	fmt.Println(releases)
	return releases, nil
}

func ApiStatusCheck() error { //TODO: get available endpoints and save them into config
	_, body, err := fasthttp.Get(nil, apiUrl+"/api/v1/app/status")
	if err != nil {
		return err
	}
	var status ApiStatus
	if err = json.Unmarshal(body, &status); err != nil {
		return fmt.Errorf("[ApiStatusCheck] %v", err)
	}
	if !status.IsAlive {
		return fmt.Errorf("[ApiStatusCheck] API is not alive")
	}
	return nil
}
func GetPosterImage(imageUrl string) (string, error) {
	hash := sha256.Sum256([]byte(imageUrl))
	filename := hex.EncodeToString(hash[:]) + ".jpg"
	savePath, err := xdg.CacheFile(filepath.Join("anilibriagtk", "images", filename))
	if err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	if _, err := os.Stat(savePath); err == nil {
		return savePath, nil
	}
	_, body, err := fasthttp.Get(nil, apiUrl+imageUrl)
	if err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	if err := os.WriteFile(savePath, body, 0644); err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	return savePath, nil
}

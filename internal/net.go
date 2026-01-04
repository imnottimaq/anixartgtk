package internal

import (
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io"
	"os"
	"path/filepath"

	"net/http"

	"github.com/adrg/xdg"
	"github.com/goccy/go-json"
)

var apiUrl string = "https://api-s.anixsekai.com"
var alternativeApiUrl string = "https://mirror-s.anixmirai.com"

func GetLatestReleases() (LatestReleases, error) { //TODO: better error handling
	resp, err := http.Get(apiUrl + "/filter/0")
	if err != nil {
		return LatestReleases{}, fmt.Errorf("[GetLatestReleases] %v", err)
	}
	defer resp.Body.Close()
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return LatestReleases{}, err
	}
	var releases LatestReleases
	if err := json.Unmarshal(body, &releases); err != nil {
		print(apiUrl)
		return LatestReleases{}, fmt.Errorf("[GetLatestReleases] %v", err)
	}
	print("OK\n")
	return releases, nil
}

func GetDetailedReleaseInfo(id string) (ReleaseDetailedResponse, error) {
	if id == "" {
		return ReleaseDetailedResponse{}, fmt.Errorf("id is empty")
	}

	resp, err := http.Get(apiUrl + "/release/" + id)
	if err != nil {
		return ReleaseDetailedResponse{}, err
	}
	defer resp.Body.Close()
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return ReleaseDetailedResponse{}, err
	}
	var info ReleaseDetailedResponse
	if err := json.Unmarshal(body, &info); err != nil {
		return ReleaseDetailedResponse{}, err
	}

	return info, nil
}

func GetPosterImage(imageName string) (string, error) {
	hash := sha256.Sum256([]byte(imageName))
	posterApiURL := alternativeApiUrl
	if posterApiURL == "" {
		posterApiURL = apiUrl
	}
	filename := hex.EncodeToString(hash[:])
	savePath, err := xdg.CacheFile(filepath.Join("anixartgtk", "images", filename))
	if err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	if _, err := os.Stat(savePath); err == nil {
		return savePath, nil
	}
	posterURL := posterApiURL + "/posters/" + imageName + ".jpg"
	resp, err := http.Get(posterURL)
	if err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v (url=%s)", err, posterURL)
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusOK {
		return "", fmt.Errorf("[GetPosterImage] unexpected status: %s (url=%s)", resp.Status, posterURL)
	}
	data, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	if len(data) == 0 {
		return "", fmt.Errorf("[GetPosterImage] empty image data")
	}
	if err := os.WriteFile(savePath, data, 0644); err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	return savePath, nil
}

func GetDubProvidersForEpisode(id string) (DubProvidersResponse, error) {
	if id == "" {
		return DubProvidersResponse{}, fmt.Errorf("id is empty")
	}

	resp, err := http.Get(apiUrl + "/episode/" + id)
	if err != nil {
		return DubProvidersResponse{}, err
	}
	defer resp.Body.Close()
	data, err := io.ReadAll(resp.Body)
	var providers DubProvidersResponse
	if err != nil {
		return DubProvidersResponse{}, err
	}
	if err := json.Unmarshal(data, &providers); err != nil {
		return DubProvidersResponse{}, err
	}
	return providers, nil
}

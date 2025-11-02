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

var apiUrl string = "https://api-s.anixsekai.com"
var alternativeApiUrl string = "https://mirror-s.anixmirai.com" //TODO: move this into config for user to be able to change it if for example main server isnt available in their region

func GetLatestReleases() (LatestReleases, error) { //TODO: better error handling
	_, body, err := fasthttp.Get(nil, apiUrl+"/filter/0")
	if err != nil {

		return LatestReleases{}, fmt.Errorf("[GetLatestReleases] %v", err)
	}
	var releases LatestReleases
	if err = json.Unmarshal(body, &releases); err != nil {
		print(fmt.Sprintf("%v", releases))
		print(apiUrl)
		return LatestReleases{}, fmt.Errorf("[GetLatestReleases] %v", err)
	}
	return releases, nil
}

/*func ApiStatusCheck() error { //TODO: remake using alternative from anixart
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
}*/

func GetPosterImage(imageName string) (string, error) {
	hash := sha256.Sum256([]byte(imageName))
	currentApiUrl := new(string)
	if ConfigData.UseAlternativeConnection {
		currentApiUrl = &alternativeApiUrl
	} else {
		currentApiUrl = &apiUrl
	}
	filename := hex.EncodeToString(hash[:])
	savePath, err := xdg.CacheFile(filepath.Join("anixartgtk", "images", filename))
	if err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	if _, err := os.Stat(savePath); err == nil {
		return savePath, nil
	}
	_, body, err := fasthttp.Get(nil, *currentApiUrl+"/posters/"+imageName+".jpg")
	if err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	if err := os.WriteFile(savePath, body, 0644); err != nil {
		return "", fmt.Errorf("[GetPosterImage] %v", err)
	}
	return savePath, nil
}

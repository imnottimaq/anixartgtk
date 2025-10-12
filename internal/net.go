package internal

import (
	"fmt"
	"github.com/goccy/go-json"
	"github.com/valyala/fasthttp"
)

var apiUrl string = "https://anilibria.top/api/v1" //TODO: move this into config for user to be able to change it if for example main server isnt available in their region

func GetLatestReleases() ([]Release, error) { //TODO: better error handling
	if err := ApiStatusCheck(); err != nil {
		return nil, err
	}
	_, body, err := fasthttp.Get(nil, apiUrl+"/anime/releases/latest?limit=10")
	if err != nil {
		return nil, err
	}
	var releases []Release
	if err = json.Unmarshal(body, &releases); err != nil {
		return nil, err
	}
	fmt.Println(releases)
	return releases, nil
}

func ApiStatusCheck() error { //TODO: get available endpoints and save them into config
	_, body, err := fasthttp.Get(nil, apiUrl+"/app/status")
	if err != nil {
		return err
	}
	var status ApiStatus
	if err = json.Unmarshal(body, &status); err != nil {
		return err
	}
	if !status.IsAlive {
		return fmt.Errorf("API is not alive")
	}
	return nil
}

func GetPosterImage() error {
	return nil //Stub func for now
}

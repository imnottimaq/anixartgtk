package internal

import "net/http"

type Config struct {
	UseAlternativeConnection bool `json:"use_alternative_connection"`
}

type HttpResult struct {
	Resp *http.Response
	Err  error
}

type LatestReleases struct {
	Releases []Release `json:"content"`
}

type Release struct {
	Id              int     `json:"id"`
	PosterCacheName string  `json:"poster"`
	Grade           float32 `json:"grade"`
	Description     string  `json:"description"`
	Name            string  `json:"title_ru"`
}

type ReleaseDetailedResponse struct { // TODO: fully parse request into that struct
	Code    int `json:"code"`
	Release struct {
		Id              int    `json:"id"`
		Name            string `json:"title_ru"`
		NameOriginal    string `json:"title_original"`
		PosterCacheName string `json:"poster"`
		Country         string `json:"country"`
		Year            string `json:"year"`

		EpisodesReleased int `json:"episodes_released"`
		EpisodesTotal    int `json:"episodes_total"`

		Duration    int    `json:"duration"`
		ReleaseDay  int    `json:"broadcast"`
		Source      string `json:"source"`
		Studio      string `json:"studio"`
		Author      string `json:"author"`
		Description string `json:"description"`
	} `json:"release"`
}

type DubProvidersResponse struct {
	Providers []DubProvider `json:"types"`
}

type DubProvider struct {
	Id            int    `json:"id"`
	Name          string `json:"name"`
	Cast          string `json:"workers"`
	EpisodesCount int    `json:"episodes_count"`
	ViewCount     int    `json:"view_count"`
}

type SourceListResponse struct{
	SourceList []Source `json:"sources"`
}

type Source struct{
	Id int `json:"id"`
	Type struct{
		Name string `json:"name"`
	}
	Name string `json:"name"`
}

type EpisodesForSourceResponse struct{
	Episodes []Episode `json:"episodes"`
}

type Episode struct{
	Name string `json:"name"`
	Url string `json:"url"`
	IsIframe bool `json:"iframe"`
}

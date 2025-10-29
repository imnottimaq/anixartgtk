package internal

/* type Release struct { //TODO: implement full release struct
	Id   int `json:"id"`
	Type struct {
		Rus string `json:"description"`
		Eng string `json:"value"`
	} `json:"type"`
	Year int `json:"year"`
	Name struct {
		Rus         string `json:"main"`
		Eng         string `json:"english"`
		Alternative string `json:"alternative"`
	} `json:"name"`
	Alias  string `json:"alias"`
	Season struct {
		Rus string `json:"description"`
		Eng string `json:"value"`
	} `json:"season"`
	Poster struct {
		Main string `json:"src"`
		Webp struct {
			Main string `json:"src"`
		} `json:"optimized"`
	} `json:"poster"`
	Ongoing   bool `json:"is_ongoing"`
	AgeRating struct {
		Adult            bool   `json:"is_adult"`
		Description      string `json:"description"`
		ShortDescription string `json:"label"`
	} `json:"age_rating"`
	PublishDay struct {
		Day string `json:"description"`
	} `json:"publish_day"`
	Description   string `json:"description"`
	EpisodesTotal int    `json:"episodes_total"`
}

type ApiStatus struct {
	IsAlive   bool     `json:"is_alive"`
	Endpoints []string `json:"available_api_endpoints"`
}

type Config struct {
	Endpoints []string `json:"endpoints"`
}*/


type LatestReleases struct {
	Releases []Release `json:"content"`
}

type Release struct {
	Id int `json:"id"`
	Poster string `json:"image"`
	PosterCacheName string `json:"poster"`
	Source string `json:"source"`
	Year string `json:"year"`
	Description string `json:"description"`
	Name string `json:"title_ru"`
}

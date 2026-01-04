package internal

// Config-related code disabled.
/*
import (
	"os"

	"github.com/adrg/xdg"
	"github.com/goccy/go-json"
)

var ConfigData *Config

func EditConfig(input Config) error {
	print("[EditConfig] Searching for config file...\n")
	configPath, err := xdg.ConfigFile("anixartgtk/config.json")
	if err != nil {
		return err
	}
	print("[EditConfig] Config file found. Trying to read it...\n")
	data, err := os.ReadFile(configPath)
	if err != nil {
		return err
	}
	var config Config
	print("[EditConfig] Parsing...\n")
	if err := json.Unmarshal(data, &config); err != nil {
		return err
	}
	print("[EditConfig] Successfully parsed config.\n[EditConfig] Editing config...\n")
	print("[EditConfig] Config edited.\n[EditConfig] Flushing it to file...\n")
	configJson, err := json.Marshal(config)
	if err != nil {
		return err
	}
	if err := os.WriteFile(configPath, configJson, 0666); err != nil {
		return err
	}
	print("[EditConfig] Successfully flushed it to file.\n")
	return nil
}

func ParseConfig() (*Config, error) {
	print("[ParseConfig] Trying to find config file...\n")
	configPath, err := xdg.ConfigFile("anixartgtk/config.json")
	if err != nil {
		return nil, err
	}
	print("[ParseConfig] Config file found. Trying to read it...\n")
	data, err := os.ReadFile(configPath)
	if err != nil {
		return nil, err
	}
	var config Config
	print("[ParseConfig] Trying to parse...\n")
	if err := json.Unmarshal(data, &config); err != nil {
		return nil, err
	}
	print("[ParseConfig] Successfully parsed config.\n")
	return &config, nil
}
*/

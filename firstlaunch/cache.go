package firstlaunch

import "github.com/adrg/xdg"

func setupCache() (string, error){
	cacheDir,err := xdg.CacheFile("anilibriagtk")
	if err != nil{
		return "",err
	}
	return cacheDir,nil
}

func setupConfigDir() (string,error){
	configDir,err := xdg.ConfigFile("anilibriagtk")
	if err != nil{
		return "",err
	}
	return configDir,nil
}
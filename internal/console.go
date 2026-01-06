package internal

import (
	"os"
	"syscall"
)

func AttachConsoleIfPresent() {
	kernel32 := syscall.NewLazyDLL("kernel32.dll")
	attachConsole := kernel32.NewProc("AttachConsole")
	const attachParentProcess = ^uintptr(0)
	r1, _, _ := attachConsole.Call(attachParentProcess)
	if r1 == 0 {
		return
	}
	conOut, err := os.OpenFile("CONOUT$", os.O_WRONLY, 0)
	if err != nil {
		return
	}
	os.Stdout = conOut
	os.Stderr = conOut
}

# SimplePrintHelper

**Original, open Android printing helper**

A clean, modern Android app that lets you print images and PDFs from your phone using the **official Android Print framework**.

It works with:
- USB printers connected via OTG cable (when Android or a manufacturer Print Service recognizes them)
- Wi-Fi / Mopria / AirPrint-compatible printers that appear in the system print dialog

This is **not** a clone of PrinterShare, NokoPrint, or any commercial app. It is original code that relies on Android’s built-in printing system.

## Features

- Pick an image → print with `PrintHelper`
- Pick a PDF → print with a proper `PrintDocumentAdapter`
- System print dialog handles printer discovery (USB + network)
- Modern Kotlin + Material 3 UI
- Minimum SDK 24 (Android 7.0)

## How to build

1. Clone the repo
2. Open in **Android Studio** (Hedgehog or newer recommended)
3. Let Gradle sync
4. Run on a device or emulator

For best USB results:
- Use a proper USB-OTG adapter
- Make sure the printer is powered on and connected
- Some printers work better if you also install the manufacturer’s official print service from Google Play

## Limitations (honest)

Android does **not** give apps unrestricted low-level USB printer driver access.  
Full support for every obscure USB printer requires proprietary drivers that commercial apps embed.  
This project stays clean and legal by using only the public Print framework.

## License

MIT – do whatever you want with it.

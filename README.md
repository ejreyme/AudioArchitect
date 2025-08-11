# AudioArchitect 🎵

A modern, cross-platform audio player built with Kotlin Multiplatform and Compose Multiplatform. AudioArchitect provides a clean, intuitive interface for playing and managing your music collection.

## Features ✨

- **Cross-Platform**: Runs on Desktop (JVM) and Web (WebAssembly)
- **Modern UI**: Built with Compose Multiplatform for a consistent, beautiful interface
- **Audio Playback**: Powered by VLC media player for reliable audio support
- **Playlist Management**: Create and manage your music playlists
- **Volume Control**: Integrated audio controls and volume management
- **Track Progress**: Real-time position tracking and seeking

## For Users 👥

### Prerequisites

- Java 11 or higher
- VLC Media Player (for desktop version)

### Installation

#### Desktop Application
1. Download the latest release from the [releases page](../../releases)
2. Extract the archive to your preferred location
3. Run the executable file

#### Web Application
Visit our [web version](https://your-domain.com/audioarchitect) - no installation required!

### Usage

1. **Playing Music**: Click the play button and select your audio files
2. **Creating Playlists**: Use the playlist manager to organize your favorite tracks
3. **Volume Control**: Adjust volume using the slider in the player controls
4. **Track Navigation**: Use the progress slider to seek through tracks

## For Developers 🚀

### Project Structure

AudioArchitect/ ├── composeApp/ # Main Compose Multiplatform application │ ├── src/commonMain/ # Shared UI and business logic │ ├── src/jvmMain/ # Desktop-specific code │ └── src/wasmJsMain/ # Web-specific code ├── shared/ # Shared Kotlin multiplatform code ├── server/ # Backend server (Ktor) └── gradle/ # Gradle wrapper and configuration

### Tech Stack

- **Kotlin Multiplatform**: Cross-platform development
- **Compose Multiplatform**: UI framework
- **VLC-J**: Audio playback for desktop
- **Ktor**: Backend server
- **Gradle**: Build system

### Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/AudioArchitect.git
   cd AudioArchitect
   ```

2. **Install prerequisites**
    - JDK 11 or higher
    - VLC Media Player
    - IntelliJ IDEA (recommended) or Android Studio

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run desktop application**
   ```bash
   ./gradlew :composeApp:run
   ```

5. **Run web application**
   ```bash
   ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
   ```

6. **Run server**
   ```bash
   ./gradlew :server:run
   ```

### Architecture

The project follows a clean architecture pattern:

- **UI Layer**: Compose Multiplatform screens and components
- **Domain Layer**: Business logic and use cases
- **Data Layer**: Audio player services and data management

Key components:
- `AudioPlayerService`: Interface for audio playback operations
- `VlcAudioPlayerService`: VLC-based implementation for desktop
- `YmePlaylist` & `YmeTrack`: Data models for music management
- `YmeAudioPlayerScreen`: Main player UI component

### Contributing Guidelines

#### Getting Started
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Write or update tests
5. Ensure code follows project conventions
6. Submit a pull request

#### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused
- Use dependency injection where appropriate

#### Pull Request Process
1. Update documentation if needed
2. Add tests for new functionality
3. Ensure all tests pass (`./gradlew test`)
4. Update CHANGELOG.md if applicable
5. Request review from maintainers

#### Areas for Contribution
- **Audio Format Support**: Add support for additional audio formats
- **UI/UX Improvements**: Enhance the user interface and experience
- **Performance Optimization**: Improve audio playback performance
- **Testing**: Increase test coverage
- **Documentation**: Improve code and user documentation
- **Accessibility**: Add accessibility features
- **Platform Support**: Extend support to mobile platforms

### Building for Production

#### Desktop
```bash
./gradlew :composeApp:packageDistributionForCurrentOS
```

#### Web
```bash
./gradlew :composeApp:wasmJsBrowserDistribution
```

#### Testing
Run all tests:

```bash
./gradlew test
```

Run specific test suites:

```bash
./gradlew :composeApp:test
./gradlew :shared:test
./gradlew :server:test
```

### Debugging
#### Desktop Application
- Use IntelliJ IDEA's debugger with the configuration `run`
- Enable VLC logging for audio issues

#### Web Application
- Use browser developer tools
- Check console for JavaScript errors

## Troubleshooting 🔧
### Common Issues
**VLC not found error**
- Ensure VLC Media Player is installed
- Add VLC installation directory to system PATH

**Build failures**
- Clean and rebuild: `./gradlew clean build`
- Check Java version: `java -version`
- Verify Gradle wrapper: `./gradlew --version`

**Audio playback issues**
- Check audio file formats are supported
- Verify system audio settings
- Test with different audio files

## License 📄

This project is licensed under the [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-nc-sa/4.0/).

### What this means:
- ✅ **Share** — copy and redistribute the material in any medium or format
- ✅ **Adapt** — remix, transform, and build upon the material
- ❌ **NonCommercial** — You may not use the material for commercial purposes
- ✅ **ShareAlike** — If you remix, transform, or build upon the material, you must distribute your contributions under the same license

For commercial licensing inquiries, please contact [your-email@example.com].

## Acknowledgments 🙏
- [VLC Media Player](https://www.videolan.org/vlc/) for audio playback capabilities
- [JetBrains](https://www.jetbrains.com/) for Kotlin and Compose Multiplatform
- [Caprica Software](https://www.capricasoftware.co.uk) for VLC-J bindings

Made with ❤️ by the AudioArchitect team


# RemoteShareSystem

A Java-based remote desktop sharing and control system that allows you to monitor and control a remote computer's screen from another machine over a network connection.

## 🚀 Features

- **Real-time Screen Sharing**: View the remote desktop in real-time with live screen capture
- **Remote Control**: Control the remote computer using mouse and keyboard input
- **Secure Connection**: Password-protected connections for security
- **Cross-platform**: Built with Java, runs on Windows, Linux, and macOS
- **Lightweight**: Minimal resource usage with efficient screen capture
- **Easy Setup**: Simple client-server architecture with straightforward configuration

## 🏗️ Architecture

The system consists of two main components:

### Server Module
- **Screen Capture**: Captures the remote desktop screen using Java AWT
- **Input Handling**: Receives and processes mouse/keyboard input from client
- **Network Server**: Manages client connections using Java NIO
- **Authentication**: Handles password-based authentication

### Client Module
- **Display Interface**: Shows the remote desktop in a scrollable window
- **Input Forwarding**: Sends mouse and keyboard events to the server
- **Connection Management**: Handles network communication with the server
- **User Interface**: Provides intuitive controls for screen sharing

## 🛠️ Technology Stack

- **Language**: Java
- **UI Framework**: Java Swing (AWT)
- **Networking**: Java NIO (Non-blocking I/O)
- **Screen Capture**: Java Robot class
- **Image Processing**: Java ImageIO
- **Build System**: Standard Java compilation

## 📋 System Requirements

- **Java Runtime Environment**: JRE 8 or higher
- **Operating System**: Windows, Linux, or macOS
- **Memory**: Minimum 256MB RAM
- **Network**: TCP/IP network connection between client and server
- **Display**: Graphics capability for screen capture and display

## 🚀 Quick Start

### Prerequisites
1. Install Java Runtime Environment (JRE) 8 or higher
2. Ensure both machines are connected to the same network

### Server Setup
1. Navigate to the project directory
2. Compile the server code:
   ```bash
   javac -d bin src/com/ccit/server/*.java src/com/ccit/recoder/*.java src/com/ccit/util/*.java
   ```
3. Run the server:
   ```bash
   java -cp bin com.ccit.server.Server
   ```
4. Set a password when prompted
5. The server will wait for client connections

### Client Setup
1. Compile the client code:
   ```bash
   javac -d bin src/com/ccit/client/*.java src/com/ccit/util/*.java
   ```
2. Run the client:
   ```bash
   java -cp bin com.ccit.client.Client
   ```
3. Enter the server's IP address and password
4. Start monitoring and controlling the remote desktop

## ⚙️ Configuration

### Server Configuration (`conf.properties`)
```properties
hostName=192.168.1.103
port=9999
```

### User Configuration (`space.properties`)
```properties
password=123
username=xaccit
```

## 📖 Usage Guide

1. **Start the Server**: Run the server application on the computer you want to control remotely
2. **Set Password**: Enter a secure password when prompted
3. **Start the Client**: Run the client application on your local machine
4. **Connect**: Enter the server's IP address and password
5. **Control**: Use your mouse and keyboard to control the remote desktop

### Controls
- **Mouse Movement**: Move your mouse to control the remote cursor
- **Mouse Clicks**: Left/right click to interact with remote applications
- **Keyboard Input**: Type to send keystrokes to the remote computer
- **Screen Navigation**: Use scroll bars to navigate large screens

## 🔧 Development

### Project Structure
```
src/
├── com/ccit/
│   ├── client/          # Client-side code
│   │   ├── Client.java
│   │   ├── Login.java
│   │   └── RemoteScreenFrame.java
│   ├── server/          # Server-side code
│   │   ├── Server.java
│   │   ├── AcceptData.java
│   │   └── DataPack.java
│   ├── recoder/         # Screen capture utilities
│   │   ├── Capture.java
│   │   └── RecodeScreen.java
│   └── util/            # Utility classes
│       ├── MouseHook.java
│       └── ByteIntSwitch.java
├── conf.properties      # Server configuration
└── space.properties     # User configuration
```

### Building from Source
```bash
# Compile all source files
javac -d bin -cp src src/com/ccit/**/*.java

# Run server
java -cp bin com.ccit.server.Server

# Run client
java -cp bin com.ccit.client.Client
```

## 🤝 Contributing

This is an open-source project developed as a hobby. Contributions, suggestions, and improvements are welcome! 

### Areas for Improvement
- Enhanced security features
- Better error handling
- Improved UI/UX
- Cross-platform optimizations
- Performance improvements
- Additional features (file transfer, multiple sessions, etc.)

## 📄 License

This project is open source. Please refer to the LICENSE file for more details.

## ⚠️ Disclaimer

This project is developed for educational and personal use. Please ensure you have proper authorization before using this software to access remote systems. The developers are not responsible for any misuse of this software.

## 📞 Support

For questions, issues, or contributions, please feel free to open an issue or submit a pull request.

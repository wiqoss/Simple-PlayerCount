# UniquesPlayer - Minecraft Plugin

## Overview
A Minecraft Spigot/Paper plugin that tracks unique players who have joined the server. Includes a built-in web server to display player count and an API endpoint.

## Features
- Tracks all unique players who join the server
- `/uniquesplayer` command to view all unique players in-game
- Built-in web server with animated Minecraft-styled display
- API endpoint for external integrations

## Project Structure
```
src/main/java/com/uniquesplayer/
├── UniquesPlayer.java      # Main plugin class
├── UniquesPlayerCommand.java   # Command handler
└── WebServer.java          # Built-in HTTP server

src/main/resources/
├── config.yml              # Configuration file
└── plugin.yml              # Plugin metadata
```

## Configuration
Edit `plugins/UniquesPlayer/config.yml`:
```yaml
web-server:
  port: 19069  # Change this to your preferred port
```

## Web Access
- **Web Page**: `http://your-server-ip:19069` - Shows animated player count
- **API**: `http://your-server-ip:19069/api` - Returns JSON with player count

## Commands
- `/uniquesplayer` (aliases: `/up`, `/uniqueplayers`) - Shows list of all unique players

## Building
Run `mvn clean package` to build the plugin JAR file.
The compiled plugin will be in `target/UniquesPlayer-1.0.0.jar`

## Recent Changes
- January 2026: Initial creation with unique player tracking, web server, and API

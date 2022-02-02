# SharedBoard-Desktop

The main purpose of this software is to help students and teachers keep learning while being on quarantine.

This is a DESKTOP app.

## You can:

- Create board
- Join board
- Multiple users support
- Choose different colors
- Erase drawings and clear board

## How it is made

- Stack = Kotlin + TornadoFX
- Server on Java Sockets
- Client on Java Sockets + TornadoFX
- When data about drawings transfers between server and client it is encoded to save traffic
- Server saves drawings in chronologic order even if ping to different clients varies. So if you reload client app you will synchronise your picture with server's

### Ways to improve

- Add synchronisation so if ping to different clients varies image on clients' borads is synchronised automatically and they don't have to reload app
- Generate timestamps with bitmap image so picture won't load drawing-by-drawing when new client starts app

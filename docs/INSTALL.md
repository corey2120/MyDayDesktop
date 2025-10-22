# MyDay Desktop - Installation Guide

## Quick Install

### Option 1: AppImage (Recommended for Most Users)

**Easiest method - no installation required!**

```bash
# Download the AppImage
wget https://[your-url]/MyDayDesktop-1.0.2-x86_64.AppImage

# Make it executable
chmod +x MyDayDesktop-1.0.2-x86_64.AppImage

# Run it
./MyDayDesktop-1.0.2-x86_64.AppImage
```

**Optional: Integrate with desktop**
```bash
# Move to applications folder
mv MyDayDesktop-1.0.2-x86_64.AppImage ~/.local/bin/

# Create desktop entry
cat > ~/.local/share/applications/myday.desktop <<EOF
[Desktop Entry]
Type=Application
Name=MyDay Desktop
Exec=$HOME/.local/bin/MyDayDesktop-1.0.2-x86_64.AppImage
Icon=myday
Terminal=false
Categories=Office;Calendar;
EOF
```

---

### Option 2: Tarball (Traditional Installation)

**For system-wide installation or custom setups**

```bash
# Download and extract
wget https://[your-url]/MyDayDesktop-1.0.2-linux.tar.gz
tar -xzf MyDayDesktop-1.0.2-linux.tar.gz

# Move to /opt (optional, but recommended)
sudo mv MyDayDesktop-1.0.2 /opt/

# Create symlink for easy access
sudo ln -s /opt/MyDayDesktop-1.0.2/bin/myday /usr/local/bin/myday

# Run the application
myday
```

**Desktop Integration:**
```bash
# Edit the desktop file
sudo nano /opt/MyDayDesktop-1.0.2/MyDayDesktop.desktop

# Update paths:
Exec=/opt/MyDayDesktop-1.0.2/bin/myday
Icon=/opt/MyDayDesktop-1.0.2/icons/icon.png

# Copy to applications
sudo cp /opt/MyDayDesktop-1.0.2/MyDayDesktop.desktop \
    /usr/share/applications/
```

---

## Prerequisites

### Java Runtime Environment

**Check if Java is installed:**
```bash
java -version
```

You should see version 17 or higher.

**If not installed:**

**Fedora/RHEL:**
```bash
sudo dnf install java-17-openjdk
```

**Ubuntu/Debian:**
```bash
sudo apt install openjdk-17-jre
```

**Arch Linux:**
```bash
sudo pacman -S jre17-openjdk
```

---

## Verification

**Test the installation:**
```bash
# For AppImage
./MyDayDesktop-1.0.2-x86_64.AppImage

# For Tarball
myday  # or /opt/MyDayDesktop-1.0.2/bin/myday
```

The application should launch with the main window.

---

## First Run Setup

1. **Create First Task List**: Click the + button
2. **Add a Task**: Use the floating action button
3. **Explore Settings**: Click the gear icon
4. **Optional: Google Calendar**: Follow GOOGLE_CALENDAR_SETUP.md

---

## Troubleshooting

### AppImage shows "libfuse2 not found"
This is usually just a warning and can be ignored. If it prevents launch:

```bash
# Fedora
sudo dnf install fuse

# Ubuntu/Debian
sudo apt install libfuse2
```

### "Java not found" error
Install Java 17+ as shown in Prerequisites section.

### Application won't start
Check the console output:
```bash
java -jar /path/to/MyDayDesktop-1.0.2.jar
```

### Desktop integration doesn't work
Update paths in the .desktop file and run:
```bash
update-desktop-database ~/.local/share/applications/
```

---

## Updating

### AppImage
Simply download the new version and replace the old file.

### Tarball
1. Back up your data (Settings → Backup & Export)
2. Remove old installation
3. Install new version
4. Restore your backup

**Note:** Your data is stored in `~/.myday/` and won't be deleted.

---

## Uninstallation

### AppImage
```bash
rm ~/.local/bin/MyDayDesktop-1.0.2-x86_64.AppImage
rm ~/.local/share/applications/myday.desktop
```

### Tarball
```bash
sudo rm -rf /opt/MyDayDesktop-1.0.2
sudo rm /usr/local/bin/myday
sudo rm /usr/share/applications/MyDayDesktop.desktop
```

### Remove Data (Optional)
```bash
rm -rf ~/.myday
```

---

## Support

For issues, check:
- `README.txt` in the distribution
- `RELEASE_NOTES_1.0.2.md`
- Documentation in `doc/` folder

---

**Version:** 1.0.2
**Last Updated:** October 22, 2025

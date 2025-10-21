# MyDay Desktop - Distribution Guide

## 📦 Available Packages

MyDay Desktop is now available in two distribution formats:

### 1. AppImage (Recommended) ✨
**File:** `MyDay-1.0.0-x86_64.AppImage`  
**Size:** 63 MB

**Advantages:**
- ✅ Single file, no installation needed
- ✅ Works on any Linux distribution
- ✅ Portable - run from USB drive
- ✅ Self-contained with all dependencies
- ✅ Easy to update (just replace the file)

**How to Use:**
```bash
# Make executable (first time only)
chmod +x MyDay-1.0.0-x86_64.AppImage

# Run
./MyDay-1.0.0-x86_64.AppImage
```

**Optional: Integration with System**
Using [Gear Lever](https://flathub.org/apps/it.mijorus.gearlever):
```bash
# Install Gear Lever
flatpak install flathub it.mijorus.gearlever

# Open Gear Lever and add the AppImage
# It will appear in your application menu
```

### 2. Tarball (Traditional)
**File:** `MyDay-1.0.0-linux.tar.gz`  
**Size:** 63 MB

**Advantages:**
- ✅ Traditional Linux package format
- ✅ Can be installed system-wide
- ✅ Easy to inspect contents
- ✅ Familiar to Linux users

**How to Use:**
```bash
# Extract
tar xzf MyDay-1.0.0-linux.tar.gz

# Run
cd MyDay-1.0.0-linux
./myday.sh
```

**Optional: System-Wide Installation**
```bash
# Install to /opt
sudo cp -r MyDay-1.0.0-linux /opt/myday

# Create symlink
sudo ln -s /opt/myday/myday.sh /usr/local/bin/myday

# Now run from anywhere
myday
```

## 🚀 Quick Start

### First Time Setup

1. **Download** your preferred format (AppImage recommended)

2. **Extract** (tarball only):
   ```bash
   tar xzf MyDay-1.0.0-linux.tar.gz
   ```

3. **Make executable** (AppImage only):
   ```bash
   chmod +x MyDay-1.0.0-x86_64.AppImage
   ```

4. **Run**:
   - AppImage: `./MyDay-1.0.0-x86_64.AppImage`
   - Tarball: `./MyDay-1.0.0-linux/myday.sh`

### System Requirements

- **OS:** Any modern Linux distribution
- **Java:** Version 17 or higher
- **Display:** X11 or Wayland
- **RAM:** 512 MB minimum, 1 GB recommended
- **Disk:** 200 MB for installation + data storage

**Check Java version:**
```bash
java -version
```

If you don't have Java 17+, install it:
```bash
# Ubuntu/Debian
sudo apt install openjdk-17-jre

# Fedora
sudo dnf install java-17-openjdk

# Arch
sudo pacman -S jre17-openjdk
```

## 📂 What's Included

### AppImage Contents
- MyDay application (all-in-one executable)
- All required Java libraries
- Application metadata and icon

### Tarball Contents
```
MyDay-1.0.0-linux/
├── myday.jar          # Application JAR file
├── myday.sh           # Launcher script
├── README.md          # User guide
├── QUICKSTART.md      # Quick start guide
├── PROJECT_SUMMARY.md # Technical details
└── INSTALL.txt        # Installation instructions
```

## 💾 Data Storage

Both packages store your data in the same location:
```
~/.myday/data.json
```

This means you can switch between AppImage and tarball without losing data!

## 🔄 Updating

### AppImage
1. Download the new AppImage
2. Replace the old one
3. Your data is safe in `~/.myday/`

### Tarball
1. Extract the new version
2. Optionally remove the old version
3. Your data is safe in `~/.myday/`

## 🗑️ Uninstalling

### AppImage
```bash
# Remove the AppImage file
rm MyDay-1.0.0-x86_64.AppImage

# Optional: Remove your data
rm -rf ~/.myday
```

### Tarball (Local)
```bash
# Remove the directory
rm -rf MyDay-1.0.0-linux

# Optional: Remove your data
rm -rf ~/.myday
```

### Tarball (System-Wide)
```bash
# Remove installation
sudo rm -rf /opt/myday
sudo rm /usr/local/bin/myday

# Optional: Remove your data
rm -rf ~/.myday
```

## 🎯 Choosing the Right Format

**Choose AppImage if:**
- ✅ You want the simplest option
- ✅ You're new to Linux
- ✅ You want portability
- ✅ You don't want to install system-wide

**Choose Tarball if:**
- ✅ You prefer traditional Linux packages
- ✅ You want system-wide installation
- ✅ You need to inspect the contents
- ✅ You're managing multiple users

## 🆘 Troubleshooting

### AppImage won't run
```bash
# Make sure it's executable
chmod +x MyDay-1.0.0-x86_64.AppImage

# Check if FUSE is installed (required for AppImage)
sudo apt install fuse libfuse2  # Ubuntu/Debian
sudo dnf install fuse           # Fedora
```

### "java: command not found"
Install Java 17 or higher (see System Requirements above)

### Application won't start
```bash
# Check Java version
java -version

# Should show version 17 or higher
```

### Data not saving
```bash
# Check if directory exists and is writable
ls -la ~/.myday

# If it doesn't exist, it will be created automatically
# If permissions are wrong:
chmod 755 ~/.myday
```

## 📋 Features Checklist

Both packages include the complete MyDay Desktop experience:

- ✅ **Tasks Management** - Multiple colored task lists
- ✅ **Calendar View** - Monthly calendar with task visualization
- ✅ **Notes (MyJournal-style)** - Write-first, name-later workflow
- ✅ **Rich Theming** - 5 background themes + 5 text colors
- ✅ **Font Customization** - Adjustable font sizes (12-24sp)
- ✅ **Data Persistence** - Automatic saving to JSON
- ✅ **Cross-Session** - All data preserved between runs

## 🔨 Building from Source

Want to build your own packages?

```bash
cd /home/cobrien/Projects/MyDayDesktop

# Build both AppImage and tarball
./build-package.sh all

# Or build individually
./build-package.sh appimage
./build-package.sh tarball
```

## 📝 Version Information

**Current Version:** 1.0.0  
**Release Date:** October 2024  
**Build System:** Gradle + Kotlin Compose Multiplatform  
**Package Formats:** AppImage, Tarball

## 🌟 Next Steps

After installation:

1. **Launch the app** using your chosen method
2. **Try the Notes feature** - Click Notes tab, then +
3. **Create some tasks** - Click Tasks tab, then +
4. **Explore the calendar** - See your tasks by date

Enjoy MyDay Desktop! 🎉

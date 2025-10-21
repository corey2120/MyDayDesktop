# Using MyDay Desktop with Gear Lever

## Quick Setup Guide

[Gear Lever](https://flathub.org/apps/it.mijorus.gearlever) is the recommended way to manage AppImages on Linux. It provides automatic desktop integration and makes AppImages appear in your application menu.

## Step-by-Step Installation

### 1. Install Gear Lever (if not already installed)

```bash
flatpak install flathub it.mijorus.gearlever
```

### 2. Add MyDay to Gear Lever

**Option A: Using Gear Lever GUI**
1. Open Gear Lever from your application menu
2. Click "Add AppImage" button (or drag-and-drop)
3. Navigate to: `/home/cobrien/Projects/MyDayDesktop/`
4. Select `MyDay-1.0.0-x86_64.AppImage`
5. Click "Open"

**Option B: Using Command Line**
```bash
# Copy AppImage to a permanent location (optional but recommended)
mkdir -p ~/Applications
cp /home/cobrien/Projects/MyDayDesktop/MyDay-1.0.0-x86_64.AppImage ~/Applications/

# Open Gear Lever and add the AppImage from ~/Applications/
```

### 3. Integration Complete!

After adding to Gear Lever:
- ✅ MyDay appears in your application menu
- ✅ Searchable from launcher
- ✅ Proper icon displayed
- ✅ Desktop shortcuts work
- ✅ File associations set up

## Using MyDay

### Launch from Application Menu
1. Open your application launcher (Activities, Application Menu, etc.)
2. Search for "MyDay"
3. Click to launch

### Launch from Gear Lever
1. Open Gear Lever
2. Find MyDay in your list
3. Click the "Launch" button

### Launch from Command Line
```bash
# If you copied to ~/Applications/
~/Applications/MyDay-1.0.0-x86_64.AppImage

# Or from original location
/home/cobrien/Projects/MyDayDesktop/MyDay-1.0.0-x86_64.AppImage
```

## Updating MyDay

When a new version is released:

1. **Build the new AppImage:**
   ```bash
   cd /home/cobrien/Projects/MyDayDesktop
   ./build-package.sh appimage
   ```

2. **Update in Gear Lever:**
   - Open Gear Lever
   - Remove the old MyDay entry
   - Add the new AppImage
   
   OR
   
   - Simply replace the AppImage file in ~/Applications/
   - Gear Lever will detect the change

Your data in `~/.myday/data.json` remains safe!

## Troubleshooting Gear Lever Issues

### Issue: MyDay doesn't appear in menu after adding

**Solution:**
```bash
# Update desktop database
update-desktop-database ~/.local/share/applications/

# Or logout and login again
```

### Issue: Icon doesn't show

**Solution:**
1. Remove from Gear Lever
2. Verify AppImage works: `./MyDay-1.0.0-x86_64.AppImage`
3. Re-add to Gear Lever
4. The icon should appear

### Issue: "AppImage cannot be opened"

**Solution:**
```bash
# Verify AppImage is executable
chmod +x MyDay-1.0.0-x86_64.AppImage

# Test it directly first
./MyDay-1.0.0-x86_64.AppImage

# If it works, re-add to Gear Lever
```

### Issue: Old version still showing

**Solution:**
1. Remove MyDay from Gear Lever completely
2. Close Gear Lever
3. Remove desktop file:
   ```bash
   rm ~/.local/share/applications/myday*.desktop
   ```
4. Reopen Gear Lever
5. Add the new AppImage

## Manual Desktop Integration (Without Gear Lever)

If you prefer not to use Gear Lever:

```bash
# Create desktop entry
cat > ~/.local/share/applications/myday.desktop << 'EOF'
[Desktop Entry]
Type=Application
Name=MyDay
Comment=Task manager combining Tasks, Calendar and Notes
Exec=/home/cobrien/Projects/MyDayDesktop/MyDay-1.0.0-x86_64.AppImage
Icon=myday
Categories=Office;
Terminal=false
StartupNotify=true
EOF

# Update desktop database
update-desktop-database ~/.local/share/applications/

# Extract and install icon
cd /home/cobrien/Projects/MyDayDesktop
./MyDay-1.0.0-x86_64.AppImage --appimage-extract
mkdir -p ~/.local/share/icons/hicolor/256x256/apps/
cp squashfs-root/myday.png ~/.local/share/icons/hicolor/256x256/apps/
rm -rf squashfs-root
```

## Recommended Setup

For the best experience:

1. **Create a permanent location:**
   ```bash
   mkdir -p ~/Applications
   ```

2. **Copy AppImage there:**
   ```bash
   cp /home/cobrien/Projects/MyDayDesktop/MyDay-1.0.0-x86_64.AppImage ~/Applications/
   ```

3. **Add to Gear Lever from ~/Applications/**

4. **Benefits:**
   - AppImage won't be accidentally deleted
   - Easy to find for updates
   - Gear Lever integration stays stable
   - Clean separation from development directory

## Testing the Installation

After setup:

1. **Check desktop entry:**
   ```bash
   ls ~/.local/share/applications/ | grep -i myday
   ```

2. **Search in launcher:**
   - Open your app launcher
   - Type "myday"
   - Should appear in results

3. **Launch and verify:**
   - Click to launch
   - App should open
   - Create a test note or task
   - Close and reopen
   - Data should persist

## Data Backup

Your MyDay data is stored separately from the AppImage:

```bash
# Backup your data
cp ~/.myday/data.json ~/Documents/myday-backup.json

# Restore data
cp ~/Documents/myday-backup.json ~/.myday/data.json
```

The data file survives AppImage updates!

## Why Gear Lever?

Benefits of using Gear Lever:
- ✅ Automatic desktop integration
- ✅ Manages updates easily
- ✅ Clean uninstallation
- ✅ Icon and menu entry handling
- ✅ AppImage organization
- ✅ One-click launching

---

**That's it!** MyDay should now be fully integrated into your desktop environment, accessible from your application menu just like any other installed application. 🎉

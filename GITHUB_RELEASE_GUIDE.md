# How to Add MyDay Desktop to GitHub Releases

This guide will help you create a GitHub release and upload your distribution files.

## Distribution Files Ready

You have these files ready to upload:
- `MyDay-1.0.1-x86_64.AppImage` (69 MB)
- `MyDay-1.0.1-linux.tar.gz` (68 MB)

---

## Method 1: Using GitHub Web Interface (Easiest)

### Step 1: Push Your Code to GitHub

First, make sure your code is on GitHub:

```bash
cd /home/cobrien/Projects/MyDayDesktop

# Initialize git if not already done
git init

# Add all files (except those in .gitignore)
git add .

# Create your first commit
git commit -m "Release v1.0.1 - Calendar sync, news widget, and more"

# Add your GitHub repository as remote
# Replace 'yourusername' with your actual GitHub username
git remote add origin https://github.com/yourusername/MyDayDesktop.git

# Push to GitHub
git push -u origin main
```

**Note:** If you get an error about the branch name, you might need to rename it:
```bash
git branch -M main
git push -u origin main
```

### Step 2: Create a Release on GitHub

1. **Go to your repository on GitHub:**
   - Navigate to `https://github.com/yourusername/MyDayDesktop`

2. **Click on "Releases":**
   - Look for the "Releases" link on the right sidebar
   - Or go directly to: `https://github.com/yourusername/MyDayDesktop/releases`

3. **Click "Create a new release"** or "Draft a new release"

4. **Fill in the release information:**

   **Tag version:** `v1.0.1`
   - Click "Choose a tag"
   - Type `v1.0.1`
   - Click "Create new tag: v1.0.1 on publish"

   **Release title:** `MyDay Desktop v1.0.1 - Calendar Sync & Enhanced Features`

   **Description:** (Copy this template)
   ```markdown
   ## 🎉 MyDay Desktop v1.0.1

   A comprehensive productivity desktop application combining Tasks, Calendar, Notes, and News.

   ### ✨ New Features

   - **📅 Calendar Sync** - Import/Export tasks via iCalendar (.ics) format
     - Compatible with Google Calendar, Outlook, Apple Calendar
     - Two-way sync with duplicate prevention
   - **📅 Calendar Redesign** - Switch between Month, Week, and Day views
   - **📰 Enhanced News Widget** - Clickable headlines with smart 30-minute caching
   - **🎨 Professional Icon** - High-quality 512x512 calendar-themed icon
   - **⚡ Performance Optimizations** - Faster rendering and better responsiveness

   ### 🐛 Bug Fixes

   - Fixed deprecated icon usage (AutoMirrored versions)
   - Improved calendar navigation
   - Better note date visibility on light backgrounds

   ### 📦 Installation

   #### Recommended: Using Gear Lever

   1. Install Gear Lever: `flatpak install flathub it.mijorus.gearlever`
   2. Download the AppImage below
   3. Open Gear Lever and add the AppImage
   4. Launch from your application menu!

   See the [README](https://github.com/yourusername/MyDayDesktop#installation) for detailed instructions.

   ### 📥 Downloads

   - **AppImage** - Recommended for Gear Lever integration
   - **Tarball** - Traditional archive format

   ### 📋 System Requirements

   - Linux (any modern distribution)
   - Java 17 or higher
   - X11 or Wayland

   ### 📖 Documentation

   - [README](https://github.com/yourusername/MyDayDesktop/blob/main/README.md)
   - [Gear Lever Setup Guide](https://github.com/yourusername/MyDayDesktop/blob/main/GEAR_LEVER_SETUP.md)
   - [Quick Start](https://github.com/yourusername/MyDayDesktop/blob/main/QUICKSTART.md)

   ---

   **Full Changelog:** [VERSION_1.0.1_TODO.md](https://github.com/yourusername/MyDayDesktop/blob/main/VERSION_1.0.1_TODO.md)
   ```

5. **Upload the distribution files:**
   - Scroll down to "Attach binaries by dropping them here or selecting them"
   - Drag and drop or click to select:
     - `MyDay-1.0.1-x86_64.AppImage`
     - `MyDay-1.0.1-linux.tar.gz`
   - Wait for the upload to complete (may take a few minutes for 68-69 MB files)

6. **Set as latest release:**
   - Check the box "Set as the latest release"

7. **Publish the release:**
   - Click the green "Publish release" button

### Step 3: Verify Your Release

After publishing:
1. The release will appear at `https://github.com/yourusername/MyDayDesktop/releases`
2. Download links will be automatically generated:
   - `https://github.com/yourusername/MyDayDesktop/releases/download/v1.0.1/MyDay-1.0.1-x86_64.AppImage`
   - `https://github.com/yourusername/MyDayDesktop/releases/download/v1.0.1/MyDay-1.0.1-linux.tar.gz`

---

## Method 2: Using GitHub CLI (gh)

If you have GitHub CLI installed:

```bash
# Install gh if needed
# Fedora: sudo dnf install gh
# Ubuntu: sudo apt install gh

# Login to GitHub
gh auth login

# Create a release with files
cd /home/cobrien/Projects/MyDayDesktop

gh release create v1.0.1 \
  --title "MyDay Desktop v1.0.1 - Calendar Sync & Enhanced Features" \
  --notes-file - <<'EOF'
## 🎉 MyDay Desktop v1.0.1

### ✨ New Features
- Calendar Sync with iCalendar format support
- Calendar redesign with Month/Week/Day views
- Enhanced news widget with clickable headlines
- Professional 512x512 icon
- Performance optimizations

### 🐛 Bug Fixes
- Fixed deprecated icons
- Improved calendar navigation
- Better note visibility

See [README](https://github.com/yourusername/MyDayDesktop) for full details.
EOF

# Upload the distribution files
gh release upload v1.0.1 \
  MyDay-1.0.1-x86_64.AppImage \
  MyDay-1.0.1-linux.tar.gz
```

---

## Method 3: Using Git Tags and Manual Upload

```bash
cd /home/cobrien/Projects/MyDayDesktop

# Create and push a tag
git tag -a v1.0.1 -m "Release v1.0.1 - Calendar Sync & Enhanced Features"
git push origin v1.0.1

# Then go to GitHub web interface and create release from tag
# Upload files manually as described in Method 1
```

---

## Important Notes

### Before You Push

1. **Check .gitignore:**
   ```bash
   cat .gitignore
   ```
   Make sure it excludes:
   - `*.AppImage`
   - `*.tar.gz`
   - `build/`
   - `.gradle/`
   - `*.log`

2. **Don't commit large binaries to git:**
   - Distribution files (AppImage, tarball) should ONLY be uploaded to releases
   - Never commit them to the repository itself
   - They'll make your repo huge and slow

3. **Update README URLs:**
   - Replace `yourusername` with your actual GitHub username in README.md
   - Update all download links to point to your repository

### File Size Considerations

- GitHub allows up to 2 GB per file in releases
- Your files (68-69 MB each) are well within limits
- Upload may take a few minutes depending on internet speed

### Creating Future Releases

For future versions (v1.0.2, etc.):

1. Build new distributions:
   ```bash
   ./build-package.sh all
   ```

2. Update version numbers in:
   - `build.gradle.kts`
   - `SettingsScreen.kt`
   - `build-package.sh`

3. Create a new release following the same steps above

---

## Troubleshooting

### Issue: "refused to update checkout branch"
```bash
git branch -M main
git push -u origin main --force
```

### Issue: "remote: Repository not found"
- Make sure the repository exists on GitHub
- Check that the URL is correct
- Ensure you have push permissions

### Issue: "Upload failed"
- Check your internet connection
- Try uploading files one at a time
- Make sure files aren't corrupted (run `./MyDay-1.0.1-x86_64.AppImage` to test)

### Issue: "Tag already exists"
```bash
# Delete local tag
git tag -d v1.0.1

# Delete remote tag
git push origin :refs/tags/v1.0.1

# Create new tag
git tag -a v1.0.1 -m "Release v1.0.1"
git push origin v1.0.1
```

---

## Verification Checklist

After creating your release, verify:

- [ ] Release appears in the "Releases" section
- [ ] Both files are attached and downloadable
- [ ] Download links work in README
- [ ] Release is marked as "Latest"
- [ ] Tag `v1.0.1` exists in repository
- [ ] Release description is formatted correctly
- [ ] Files download and run correctly:
  ```bash
  wget https://github.com/yourusername/MyDayDesktop/releases/download/v1.0.1/MyDay-1.0.1-x86_64.AppImage
  chmod +x MyDay-1.0.1-x86_64.AppImage
  ./MyDay-1.0.1-x86_64.AppImage
  ```

---

## Next Steps

After your release is live:

1. **Share your release:**
   - Post on social media
   - Share in Linux communities
   - Submit to app directories

2. **Monitor downloads:**
   - GitHub shows download counts on releases page

3. **Gather feedback:**
   - Enable GitHub Discussions
   - Monitor Issues for bug reports

4. **Plan next release:**
   - Track features in VERSION_1.0.2_TODO.md
   - Prioritize based on user feedback

---

**Need help?** Open an issue on GitHub or check the [GitHub Releases documentation](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository).

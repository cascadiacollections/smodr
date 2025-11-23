# Enabling GitHub Pages for Smodr Privacy Policy

This document explains how to enable GitHub Pages to host the privacy policy at `https://cascadiacollections.github.io/smodr/privacy_policy.html`.

## Steps to Enable GitHub Pages

1. **Go to Repository Settings**
   - Navigate to https://github.com/cascadiacollections/smodr/settings

2. **Access Pages Settings**
   - In the left sidebar, click on "Pages" under the "Code and automation" section

3. **Configure GitHub Pages**
   - Under "Build and deployment":
     - **Source**: Select "Deploy from a branch"
     - **Branch**: Select `main` (or the branch where the docs are)
     - **Folder**: Select `/docs`
   - Click "Save"

4. **Wait for Deployment**
   - GitHub will automatically build and deploy the site
   - This usually takes 1-2 minutes
   - You'll see a banner with the URL once it's live

5. **Verify**
   - Visit https://cascadiacollections.github.io/smodr/privacy_policy.html
   - Ensure the page loads correctly

## What's Been Changed

The following files have been updated to use the new GitHub Pages URL:

1. `Smodr/src/main/java/com/kevintcoughlin/smodr/views/activities/MainActivity.kt`
   - Updated `PRIVACY_POLICY_URL` constant

2. `Smodr/src/main/res/xml/preferences.xml`
   - Updated privacy policy intent data

3. `docs/privacy_policy.html`
   - New comprehensive privacy policy document

4. `docs/index.html`
   - Documentation landing page

5. `docs/_config.yml`
   - Jekyll configuration for GitHub Pages

## Testing the Change

After enabling GitHub Pages and merging this PR:

1. **Test from the app**:
   - Open Smodr
   - Go to Settings menu (⋮)
   - Tap "Privacy policy"
   - Verify it opens the correct URL in a browser

2. **Test the preferences**:
   - Go to app settings/preferences
   - Find the Privacy Policy option
   - Verify it opens correctly

## Notes

- The privacy policy is mobile-responsive and will display correctly on Android devices
- The URL uses HTTPS for secure access
- The privacy policy covers Firebase Analytics usage in the app
- Contact information directs users to the GitHub issues page

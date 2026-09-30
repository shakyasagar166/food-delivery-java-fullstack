@echo off
title Push Java Full Stack Food Delivery to GitHub
color 0A

echo ================================================================
echo    PUSHING JAVA FULL STACK FOOD DELIVERY TO GITHUB
echo    Target: https://github.com/shakyasagar166/food-delivery-java-fullstack
echo ================================================================
echo.

cd /d "%~dp0"

echo [1/5] Checking Git installation...
git --version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo [ERROR] Git is not installed or not in PATH!
    echo Please install Git from https://git-scm.com/
    pause
    exit /b 1
)
echo [OK] Git is available.
echo.

echo [2/5] Initializing Git repository...
if not exist ".git" (
    git init -b main
    echo [OK] Initialized new Git repository on 'main'.
) else (
    git branch -M main
    echo [OK] Existing Git repository detected, branch set to 'main'.
)
echo.

echo [3/5] Setting up Remote Origin...
git remote remove origin >nul 2>&1
git remote add origin https://github.com/shakyasagar166/food-delivery-java-fullstack.git
echo [OK] Remote set to: https://github.com/shakyasagar166/food-delivery-java-fullstack.git
echo.

echo [4/5] Staging files and cloud Dockerfile...
git add .
git commit -m "fix(react): set CI=false in build script for Vercel deployment" || echo Already up to date
echo [OK] Commit ready.
echo.

echo [5/5] Pushing to GitHub...
echo IMPORTANT: If you haven't created the repository on GitHub yet:
echo 1. Open https://github.com/new
echo 2. Repository name: food-delivery-java-fullstack
echo 3. Keep it Public (or Private) and leave README unchecked
echo 4. Click 'Create repository'
echo.
echo Now pushing code to GitHub...
git push -u origin main --force

if errorlevel 1 (
    echo.
    echo ----------------------------------------------------------------
    echo [NOTE] If push failed:
    echo - Make sure you have created 'food-delivery-java-fullstack' on GitHub:
    echo   https://github.com/new
    echo - Then re-run this script!
    echo ----------------------------------------------------------------
) else (
    echo.
    echo ================================================================
    echo [SUCCESS] Code pushed successfully to GitHub!
    echo View your repository here:
    echo https://github.com/shakyasagar166/food-delivery-java-fullstack
    echo ================================================================
)

echo.
pause

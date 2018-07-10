sudo apt-get install cmake
sudo apt-get install freeglut3-dev
sudo apt-get install mesa-common-dev
sudo apt-get install xorg-dev libglu1-mesa-dev
sudo apt-get install libpthread-stubs0-dev
g++ -pthread Soccer-2k15.cpp -std=c++11 -lglut -lGLU -lGL -o fifa
./fifa


img=imread('C.jpg');
gray=rgb2gray(img);
A=[7,9,13;2,5,12];
[a,b]=find(A==max(max(A)));
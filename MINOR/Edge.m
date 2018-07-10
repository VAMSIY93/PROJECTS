im1=imread('G.jpg');
gray1=rgb2gray(im1);
Ed1=edge(gray1,'canny',0.25);
imtool(Ed1);

Ed2=edge(gray1,'sobel');
imtool(Ed2);

imshowpair(Ed1,Ed2,'montage');

[Gmag,Gdir]=imgradient(gray1,'sobel');

Gimg=uint8(Gmag);
Ed=edge(Gimg,'canny',0.6);
imtool(Ed);
img = imread('A.jpg');
gray = rgb2gray(img);
level = multithresh(gray);
seg_I = imquantize(gray,level);
imshow(gray);
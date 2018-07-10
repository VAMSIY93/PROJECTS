img = imread('tower_medium.jpg');
src = img;

AddRem = input('Enter 1 to add and 0 to remove: ');
vertS = input('Enter number of vertical seams: ');
horzS = input('Enter number of Horizontal seams: ');

[r,c,~] = size(img);
if vertS>1
    img = SeamGeneration(img,AddRem,vertS);
end

if horzS>1
    img = inverseImage(img);
    img = SeamGeneration(img,AddRem,horzS);
    img = inverseImage(img);
end

imshowpair(src,img,'montage');
im1=imread('A.jpg');
gray1=rgb2gray(im1);
h=fspecial('gaussian');
imf=imfilter(gray1,h);
imtool(imf);
bin2=im2bw(im1);
bin=im2bw(imf);
imshowpair(bin,bin2,'montage');
[r1,c1]=size(bin);
dim=size(bin);
col=770;
row=min(find(bin(:,col)));
boundary = bwtraceboundary(bin,[row, col],'S');
imshow(rgb2gray(im1))
hold on;
plot(boundary(:,2),boundary(:,1),'g','LineWidth',3);
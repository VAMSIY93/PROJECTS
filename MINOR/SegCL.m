im1=imread('E.jpg');
gray1=rgb2gray(im1);
[Gmag,Gdir]=imgradient(gray1,'sobel');

im1=uint8(Gmag);
imtool(im1);

Ed=edge(im1,'sobel');
BW=im2bw(Ed);

bin=im2bw(im1);
[r1,c1]=size(bin);
dim=size(bin)
col=577;
row=307;
%row=max(find(bin(:,col)));
boundary = bwtraceboundary(bin,[row, col],'S');
imshow((im1))
hold on;
plot(boundary(:,2),boundary(:,1),'g','LineWidth',3);

imtool(im1);

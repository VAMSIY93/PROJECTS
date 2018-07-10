num='6/';
c1 = imread([num 'color1.png']);
dp1 = imread([num 'depth1.png']);
ir1 = imread([num 'IR1.png']);

gray = rgb2gray(c1);
h2 = histeq(gray,256);
h3 = histeq(gray,256);
a = (dp1==0 | dp1==11);
%{
[r,c] = size(ir1);

data = importdata([num 'mesh1.dat']);
depth = data(:,3);
dep = reshape(depth,c,r)';
mx = max(max(depth));
dep = uint8(dep*255/mx);
dep = histeq(dep,256);

infrared = uint8(ir1/255.0);
infrared = histeq(infrared,256);
ed = edge(infrared);
%}
%imtool(infrared);
%imshow(ed);


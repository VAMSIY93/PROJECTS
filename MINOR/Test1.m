img = imread('blister-3.jpg');
gray = rgb2gray(img);
bin = im2bw(gray);
imshow(gray);

%{
for i=1:r
    for j=1:c
        if g1(i,j)<25
            I1(i,j)=0;
        elseif g1(i,j)>25 && g1(i,j)<75
            I1(i,j)=10;
        elseif g1(i,j)>75 && g1(i,j)<100
            I1(i,j)=g1(i,j)-50;
        elseif g1(i,j)>100 && g1(i,j)<125
            I1(i,j)=65;
        elseif g1(i,j)>125 && g1(i,j)<175
            I1(i,j)=75+(g1(i,j)-125)*3;
        else
            I1(i,j)=255;
        end    
    end
end 
%}
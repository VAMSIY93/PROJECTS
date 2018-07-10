depth = imread('depth1.png');
data = importdata('mesh1.dat');
[r,c] = size(depth);
color = data(:,4:6);
new_col = zeros(r,c,3);
for i=1:3
    new_col(:,:,i) = reshape(color(:,i),c,r)';
end
new_color = uint8(new_col);
imwrite(new_color,'c1.png');
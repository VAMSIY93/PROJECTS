num='6/';
c1 = imread([num 'color1.png']);
d1 = imread([num 'depth1.png']);
ir1 = imread([num 'IR1.png']);

[selected_pts, continueflag] = select_pt(ir1,1);
bound = uint16(selected_pts);
data = importdata([num 'mesh1.dat']);
depth = data(:,3);

[r,c] = size(d1);
depth_map = (reshape(depth,c,r))';
y_map = (reshape(data(:,2),c,r))';
x_map = (reshape(data(:,1),c,r))';
dp = zeros(4,1);
xv = zeros(4,1);
yv = zeros(4,1);
for i=1:4
    dp(i,1) = depth_map(bound(i,1),bound(i,2));
    xv(i,1) = x_map(bound(i,1),bound(i,2));
    yv(i,1) = y_map(bound(i,1),bound(i,2));
end

depth_map = depth_map*100;
for i=1:r
    for j=1:c
        if(depth_map(i,j)>255)
            depth_map(i,j)=0;
        end
    end
end

ir = uint8(ir1/255);
infrared = histeq(ir,256);

n = size(depth);
mx = max(dp);
mn = min(dp);
O = [0,0,0,0,0,0];
ymn = max(yv(3:4,1));
ymx = min(yv(1:2,1));
xmx = max(xv(2:3,1));
if yv(1,1)<xv(4,1)
    xmn = xv(1,1);
else
    xmn = xv(4,1);
end

fid = fopen([num 'new_mesh1.ply'],'w');
    fprintf(fid,'ply\nformat ascii 1.0\ncomment VCGLIB generated\nelement vertex %d\n',r*c);    
    fprintf(fid,'property float x\nproperty float y\nproperty float z\n');
    fprintf(fid,'property uchar red\nproperty uchar green\nproperty uchar blue\n');
    fprintf(fid,'element face 0\n');
    fprintf(fid,'property list uchar int vertex_indices\nend_header\n');
    for i=1:n
        fprintf(fid, '%f %f %f %d %d %d\n', data(i,1), data(i,2), data(i,3),  data(i,4),  data(i,5),  data(i,6));
    end
fclose(fid);

for i=1:n
    if depth(i)>(mx+0.01) || depth(i)<(mn-0.01)
        depth(i)=0;
        data(i,:)=O;
    end
    if data(i,2)<(ymn-0.01) || data(i,2)>(ymx+0.03)
        data(i,:)=O;
    end
    if data(i,1)<(xmn-0.03) || data(i,1)>(xmx+0.03)
        data(i,:)=O;
    end
end

dlmwrite([num 'new_mesh1.dat'],data,' ');
fid = fopen([num 'new_mesh2.ply'],'w');
    fprintf(fid,'ply\nformat ascii 1.0\ncomment VCGLIB generated\nelement vertex %d\n',r*c);    
    fprintf(fid,'property float x\nproperty float y\nproperty float z\n');
    fprintf(fid,'property uchar red\nproperty uchar green\nproperty uchar blue\n');
    fprintf(fid,'element face 0\n');
    fprintf(fid,'property list uchar int vertex_indices\nend_header\n');
    for i=1:n
        fprintf(fid, '%f %f %f %d %d %d\n', data(i,1), data(i,2), data(i,3),  data(i,4),  data(i,5),  data(i,6));
    end
fclose(fid);
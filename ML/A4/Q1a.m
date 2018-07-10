fid = fileread('digitdata.txt');
A = strsplit(fid,'\n');
[~,c] = size(A);
X = zeros(c-2,157);
P = zeros(157,1);
for i = 2:c-1
    str=cell2mat(A(1,i));
    list = strsplit(str);
    X(i-1,:)=str2double(list(1,2:158));
end

str=cell2mat(A(1,1));
list=strsplit(str);
[~,c]=size(list);
for i=1:c
    str=cell2mat(list(1,i));
    loc=strfind(str,'l');
    locs=strfind(str,'"');
    if locs(1,2)==8
        P(i,1)=str2double(str(loc+1));
    elseif locs(1,2)==9
        P(i,1)=str2double(str(loc+1:loc+2));
    else
        P(i,1)=str2double(str(loc+1:loc+3));
    end
end

fid=fileread('digitlabels.txt');
A=strsplit(fid);
[~,c1]=size(A);
C=zeros(floor(c1/2),1);
for i=3:c1
    if mod(i,2)==1
        C(floor(i/2),1)=str2double(A(1,i));
    end
end
save('data','X','C');

prompt='Enter example number:';
n=input(prompt);
img=zeros(28);
for i=1:c
    l=ceil(P(i,1)/28);
    w=mod(P(i,1),28);
    if w==0
        img(l,28)=X(n,i);
    else
        img(l,w)=X(n,i);
    end
end
imshow(img);
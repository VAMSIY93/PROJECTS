function Test2
im1=imread('A.jpg');
im2=imread('B.jpg');
im3=imread('C.jpg');
im4=imread('D.jpg');
im5=imread('E.jpg');
im6=imread('F.jpg');
im7=imread('G.jpg');
%{
imtool(im1);
imtool(im2);
imtool(im3);
imtool(im4);
imtool(im5);
%}
img=im7;
[r,c,~]=size(img);
arr=zeros(r,c);
for i=1:r
    for j=1:c
        if img(i,j,1)>200 && img(i,j,2)>200
            arr(i,j)=0;
        else
            arr(i,j)=255;
        end
    end
end

gray=rgb2gray(img);
[Gmag,~]=imgradient(gray,'sobel');
gradimg = uint8(Gmag);
arr=uint8(arr);
%arr=Functions(arr);
regNo = getStem(arr);
disp(regNo);
%{
dir=getDirection(regNo);
pixel=getStartPixel(arr,regNo,dir);
disp('pixel');
disp(pixel);
x=uint16(pixel(1,1));
y=uint16(pixel(1,2));
for i=1:3
    for j=1:3
        arr((x+2-i),(y+2-j))=128;
    end
end
arr=extractVein(gradimg,arr,pixel,dir);
%imtool(img);
%}
imtool(arr);
imtool(gradimg);
end

function dir = getDirection(regNo)
    if regNo==2 || regNo==3
        dir='down';
    elseif regNo==5 || regNo==9
        dir='right';
    elseif regNo==8 || regNo==12
        dir='left';
    elseif regNo==14 || regNo==15
        dir='up';
    else
        dir='??';
    end
end

function pixel = getStartPixel(img,regNo,dir)
    [r,c]=size(img);
    rd=r/4;
    cd=c/4;
    rst=floor((regNo-1)/4)*rd;
    rend=ceil((regNo-1)/4)*rd;
    md = mod(regNo-1,4);
    cst=md*cd;
    cend=(md+1)*cd;
    pixel = [rst+1,cst+1];
    for j=cst:cend-100
        for i=rst:rend
            if regNo==5 || regNo==9
                if img(i,j)==0 && sum(img(i,j-99:j)==zeros(1,100))==100
                    pixel=moveForward(img,dir,i,j);
                    return;
                end
            elseif regNo==8 || regNo==12
                if img(i,j)==0 && sum(eq(img(i,j:j+99),zeros(1,100)))==100
                    pixel=moveForward(img,dir,i,j);
                    return;
                end
            end
        end     
    end
end

function pixel = moveForward(img,dir,x,y)
    if strcmp(dir,'right')==1
        while(true)
            area=img(x-3:x+3,y:y+2);
            if sum(area==zeros(7,3))>0
                for i=1:3
                    if img(x,y+i)==0
                        y=y+i;
                    else
                        for j=1:3
                            if img(x-j,y+i)==0
                                x=x-j;
                                y=y+i;
                            elseif img(x+j,y+i)==0
                                x=x+j;
                                y=y+i;
                            end
                        end
                    end
                end
            else
                pixel=[x,y];
                break;
            end
        end
    elseif strcmp(dir,'left')==1
        while(true)
            area=img(x-3:x+3,y-2:y);
            if sum(area==zeros(7,3))>0
                for i=1:3
                    if img(x,y-i)==0
                        y=y-i;
                    else
                        for j=1:3
                            if img(x-j,y-i)==0
                                x=x-j;
                                y=y-i;
                            elseif img(x+j,y-i)==0
                                x=x+j;
                                y=y-i;
                            end
                        end
                    end
                end
            else
                pixel=[x,y];
                break;
            end
        end
    end
end

function img = extractVein(grad,img,pixel,dir)
    x=uint16(pixel(1,1));
    y=uint16(pixel(1,2));
    A=100*ones(3,10);
    area=grad(x-20:x+19,y-40:y-1);
    [x1,y1]=find(area==max(max(area)));
    x=x-(20-x1(1,1));
    y=y-(40-y1(1,1));
    %imtool(grad);
    if strcmp(dir,'left')==1
        while(true)
            area=grad(x-1:x+1,y-10:y-1);
            B=area>A;
            disp('A');
            disp(B);
            disp(sum(B));
            %flag=false;
            if sum(sum(B))>0
                %{
                for i=1:4
                    for j=1:5
                        disp(grad(x-(i-1),y-j));
                        if grad(x+(i-1),y-j)>100
                            disp('enter');
                            for k=1:j
                                img(x,y-k)=0;
                                img(x-1,y-k)=0;
                                img(x+1,y-k)=0;
                            end
                            x=x+(i-1);
                            y=y-j;
                            flag=true;
                            break;                     
                        elseif grad(x-(i-1),y-j)>100
                            disp('entered');
                            for k=1:j
                                img(x,y-k)=0;
                                img(x-1,y-k)=0;
                                img(x+1,y-k)=0;
                            end
                            x=x-(i-1);
                            y=y-j;
                            flag=true;
                            break;
                        end
                    end
                    if(flag)
                        break;
                    end
                end
                
                %}
                [x1,y1]=find(area==max(max(area)));
                for i=1:(11-y1(1,1))
                    img(x,y-i)=0;
                    img(x-1,y-i)=0;
                    img(x+1,y-i)=0;
                end
                x=x-(2-x1(1,1));
                y=y-(11-y1(1,1));
                img(x,y)=0;
                disp('x');
                disp(x);
                disp(y);                
            else
                break;
            end
        end
    end
end
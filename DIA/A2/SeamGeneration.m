function img = SeamGeneration(img,AddRem,k)
[~,c,~] = size(img);
Gsum = computeGradient(img);
seams = 0;
seamImg = img;

if AddRem==0
    for i=1:k
        E = Gsum;    
        [E,valid] = computeEnergy(E);
        
        if i==1
            Ener = uint8(E);
            imshow(Ener);
            title('Energy Funtion');
            pause(3);
        end
    
    
        if valid==1
            [Gsum,seamImg] = computeSeam(Gsum,E,i,seamImg);
            seams = seams+1;
        else
            img = removeSeams(img,seams,seamImg);
            seamImg = img;
            Gsum = computeGradient(img);
            E = Gsum;
            [E,~] = computeEnergy(E);
            [Gsum,seamImg] = computeSeam(Gsum,E,i,seamImg);
            seams = 1;
        end
    end
    img = removeSeams(img,seams,seamImg);
else
%{   
    for i=1:k
        E = Gsum;    
        [E,valid] = computeEnergy(E);
        if i==1
            Ener = uint8(E);
            imshow(Ener);
            title('Energy Funtion');
            pause(3);
        end
        
        if valid==1
            [Gsum,seamImg] = computeSeam(Gsum,E,i,seamImg);
            seams = seams+1;
        else
            img = addSeams(img,seams,seamImg);
            seamImg = img;
            Gsum = computeGradient(img);
            E = Gsum;
            [E,~] = computeEnergy(E);
            [Gsum,seamImg] = computeSeam(Gsum,E,i,seamImg);
            seams = 1;
        end
    end
    img = addSeams(img,seams,seamImg);
 %}   
 %  
    [E,valid] = computeEnergy(Gsum);
    Ener = uint8(E);
    imshow(Ener);
    pause(3);
    for i=1:k  
        if valid==1
            [Gsum,seamImg,E,cancel,finish] = computeSeamAdd(Gsum,E,i,seamImg,img);
            while cancel==1 && finish==0
                [Gsum,seamImg,E,cancel,finish] = computeSeamAdd(Gsum,E,i,seamImg,img);
            end
            seams = seams+1;
            if finish==1
                img = addSeams(img,seams,seamImg);
                img = img(:,1:c+seams,:);
                disp('cant find anymore seams');
                disp('No. of seams');
                disp(seams);
                return;
            end
        end
    end    
    img = addSeams(img,seams,seamImg);
    img = img(:,1:c+seams,:);
%
end
save('seam.mat','seamImg');
end    

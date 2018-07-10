function eps=computeThreshold(img)
    mn = mean(mean(img));
    md = median(median(img));
    eps=mn+md;
    if abs(eps)<0
        eps=eps*5;
    end
    if eps>0
        eps=-eps;
    end
end
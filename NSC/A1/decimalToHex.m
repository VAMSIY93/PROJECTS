function ch = decimalToHex(c)
    if c>=0 && c<=9
        ch = num2str(c);
    else
        ch = char(c+55);
    end
end
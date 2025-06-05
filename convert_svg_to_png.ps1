function Convert-SvgToPng { 
    param( 
        [string]$SvgPath, 
        [string]$PngPath, 
        [int]$Width = 200, 
        [int]$Height = 200 
    ) 
    Add-Type -AssemblyName System.Drawing 
    $bitmap = New-Object System.Drawing.Bitmap($Width, $Height) 
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap) 
    $graphics.Clear([System.Drawing.Color]::Transparent) 
    $svgContent = Get-Content $SvgPath -Raw 
    if ($svgContent -match 'fill="(#[0-9a-fA-F]{6})"') { 
        $fillColor = $matches[1] 
        $colorObj = [System.Drawing.ColorTranslator]::FromHtml($fillColor) 
        $graphics.Clear($colorObj) 
    } 
        $text = $matches[1] 
        $font = New-Object System.Drawing.Font("Arial", 24) 
        $brush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White) 
        $stringFormat = New-Object System.Drawing.StringFormat 
        $stringFormat.Alignment = [System.Drawing.StringAlignment]::Center 
        $stringFormat.LineAlignment = [System.Drawing.StringAlignment]::Center 
        $rect = New-Object System.Drawing.RectangleF(0, 0, $Width, $Height) 
        $graphics.DrawString($text, $font, $brush, $rect, $stringFormat) 
    } 
    $bitmap.Save($PngPath, [System.Drawing.Imaging.ImageFormat]::Png) 
    $graphics.Dispose() 
    $bitmap.Dispose() 
    Write-Host "Converted $SvgPath to $PngPath" 
} 
Convert-SvgToPng -SvgPath "ruoyi-ui\public\profile\garbage\recyclable\recyclable.svg" -PngPath "ruoyi-ui\public\profile\garbage\recyclable\recyclable.png" 
Convert-SvgToPng -SvgPath "ruoyi-ui\public\profile\garbage\hazardous\hazardous.svg" -PngPath "ruoyi-ui\public\profile\garbage\hazardous\hazardous.png" 
Convert-SvgToPng -SvgPath "ruoyi-ui\public\profile\garbage\kitchen\kitchen.svg" -PngPath "ruoyi-ui\public\profile\garbage\kitchen\kitchen.png" 
Convert-SvgToPng -SvgPath "ruoyi-ui\public\profile\garbage\other\other.svg" -PngPath "ruoyi-ui\public\profile\garbage\other\other.png" 

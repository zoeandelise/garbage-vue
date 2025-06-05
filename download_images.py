import os
import requests
import time
from PIL import Image, ImageDraw, ImageFont

# 创建目标目录
target_dir = ("E:/study/毕设/laji/RuoYi-Vue/ruoyi-admin/src/main/resources/"
              "static/profile/garbage")
os.makedirs(target_dir, exist_ok=True)

# 图片列表和颜色
image_list = [
    {"name": "recyclable.png", "query": "recycling symbol", "color": (0, 128, 0)},  # 绿色
    {"name": "hazardous.png", "query": "hazardous waste", "color": (255, 0, 0)},    # 红色
    {"name": "kitchen.png", "query": "food waste", "color": (128, 64, 0)},          # 棕色
    {"name": "other.png", "query": "trash waste", "color": (128, 128, 128)},        # 灰色
    {"name": "paper.png", "query": "paper waste", "color": (255, 255, 0)},          # 黄色
    {"name": "plastic.png", "query": "plastic bottle", "color": (0, 0, 255)},       # 蓝色
    {"name": "glass.png", "query": "glass bottle", "color": (128, 128, 255)},       # 淡蓝色
    {"name": "metal.png", "query": "metal can", "color": (192, 192, 192)},          # 银色
    {"name": "textile.png", "query": "textile waste", "color": (255, 128, 0)},      # 橙色
    {"name": "battery.png", "query": "battery waste", "color": (128, 0, 0)},        # 深红色
    {"name": "fluorescent.png", "query": "fluorescent lamp", "color": (0, 255, 255)}, # 青色
    {"name": "medicine.png", "query": "medicine waste", "color": (255, 0, 255)},    # 紫色
    {"name": "paint.png", "query": "paint waste", "color": (192, 0, 192)},          # 紫红色
    {"name": "leftover.png", "query": "food leftovers", "color": (128, 64, 0)},     # 棕色
    {"name": "bone.png", "query": "food bones", "color": (255, 255, 224)},          # 淡黄色
    {"name": "vegetable.png", "query": "vegetable waste", "color": (0, 255, 0)},    # 亮绿色
    {"name": "fruit_peel.png", "query": "fruit peel", "color": (255, 165, 0)},      # 橙色
    {"name": "eggshell.png", "query": "eggshell waste", "color": (255, 248, 220)},  # 奶油色
    {"name": "offal.png", "query": "offal waste", "color": (139, 69, 19)},          # 棕色
    {"name": "tissue.png", "query": "tissue paper", "color": (255, 222, 173)},      # 淡棕色
    {"name": "diaper.png", "query": "diaper waste", "color": (255, 182, 193)},      # 粉色
    {"name": "cigarette.png", "query": "cigarette butt", "color": (112, 128, 144)}, # 灰蓝色
    {"name": "ceramic.png", "query": "ceramic waste", "color": (245, 245, 220)},    # 米色
    {"name": "toilet_paper.png", "query": "toilet paper roll", "color": (255, 250, 250)} # 雪白色
]

# 创建本地占位图像
def create_local_placeholder(output_file, text, color=(200, 200, 200)):
    try:
        # 创建300x300的图像
        img = Image.new('RGB', (300, 300), color=color)
        draw = ImageDraw.Draw(img)
        
        # 尝试加载字体，如果不可用则使用默认字体
        try:
            font = ImageFont.truetype("arial.ttf", 20)
        except IOError:
            font = ImageFont.load_default()
        
        # 在图像上绘制文本
        text_width = draw.textlength(text, font=font)
        text_position = ((300 - text_width) // 2, 140)
        draw.text(text_position, text, fill=(0, 0, 0), font=font)
        
        # 保存图像
        img.save(output_file)
        print(f"Created local placeholder for {output_file}")
        return True
    except Exception as e:
        print(f"Error creating local placeholder for {output_file}: {e}")
        return False

# 从Unsplash下载图片（备用方法）
def download_from_unsplash(search_term, output_file):
    try:
        # 使用Unsplash作为备用方法
        fallback_url = (f"https://source.unsplash.com/300x300/?"
                        f"{search_term.replace(' ', '+')}")
        img_response = requests.get(fallback_url, timeout=5)
        if img_response.status_code == 200:
            with open(output_file, 'wb') as f:
                f.write(img_response.content)
            print(f"Downloaded {output_file} from Unsplash")
            return True
            
        print(f"Failed to download {search_term} from Unsplash")
        return False
    
    except Exception as e:
        print(f"Error downloading {search_term} from Unsplash: {e}")
        return False

# 下载所有图片
for img in image_list:
    output_path = os.path.join(target_dir, img["name"])
    
    # 检查文件是否已存在
    if os.path.exists(output_path):
        print(f"Skipping {img['name']} - already exists")
        continue
    
    # 尝试从Unsplash下载
    try:
        success = download_from_unsplash(img["query"], output_path)
    except:
        success = False
    
    # 如果下载失败，创建本地占位图像
    if not success:
        create_local_placeholder(output_path, img["query"], img.get("color", (200, 200, 200)))
    
    # 避免过快发送请求
    time.sleep(1)

print("All images downloaded or created!") 
import Foundation
import UIKit
import ImageIO

struct ColorSet {
    let from: CIColor
    let to: CIColor
}

struct HexColorSet {
    let from: String
    let to: String
}

struct FrameData {
    let delayTime: Double
    let frameIndex: Int}

class GifControllerView: UIImageView {
  
  // CGImageSource instance to control gif frames
  private var gifSource: CGImageSource?
  
  var displayLink: CADisplayLink?
  var index = 0
  var frameCount = 0
  var isAnimatingGif = true
  var speed = 1.0
  var isReverse = false
  var fromToColorSet: [ColorSet] = []
  let color = CIColor(red: 0.5, green: 0.5, blue: 0.5, alpha: 1.0)
  let context = CIContext(options: nil)
  
  
  // Initializer
  init() {
    super.init(image: nil)
  }
  
  @objc func setSource(_ gifUrl: String) {
    guard let gifUrl = URL(string: gifUrl) else {return}
    createGifSource(from: gifUrl)
    updateFrame()
  }
  @objc func setColorMappings(_ colorMappings: NSArray) {
    guard let array = colorMappings as? [[String: NSNumber]] else { return }

    var colorSets: [ColorSet] = []

    for item in array {
        if let fromColorString = item["from"],
           let toColorString = item["to"],
           let from = RCTConvert.uiColor(fromColorString),
           let to = RCTConvert.uiColor(toColorString) {
          colorSets.append(
            ColorSet(from: CIColor(color: from), to: CIColor(color: to))
          )
        }
    }
    fromToColorSet = colorSets
  }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    private func createGifSource(from url: URL) {
        gifSource = CGImageSourceCreateWithURL(url as CFURL, nil)
        self.frameCount = CGImageSourceGetCount(gifSource!)
    }
    
    func displayFrame(at index: Int) {
        guard let gifSource = gifSource else {
            return
        }
        guard var cgImage = CGImageSourceCreateImageAtIndex(gifSource, index, nil) else {
            return
        }
        let ciImage = CIImage(cgImage: cgImage)
        guard let output = changeColor(ciImage) else {
            return
        }
        self.image = UIImage(cgImage: context.createCGImage(output, from: ciImage.extent)!)
    }
    
    private func updateFrame() {
        if(gifSource == nil) {return}
        if(isReverse) {
            index = (index - 1 + frameCount) % frameCount
        } else {
            index = (index + 1) % frameCount
        }
        displayFrame(at:index)
        let delayTime = getDelayTime(for: self.gifSource!, index: index)
        if(isAnimatingGif) {
            DispatchQueue.main.asyncAfter(deadline: .now() + (delayTime ?? 1.0) * speed) {[weak self] in
                self?.updateFrame()
            }
        }
    }
    
    private func getDelayTime(for imageSource: CGImageSource, index: Int) -> Double? {
        guard let properties = CGImageSourceCopyPropertiesAtIndex(imageSource, index, nil) as? [String: Any],
              let gifDict = properties[kCGImagePropertyGIFDictionary as String] as? [String: Any] else {
            return nil
        }
        
        if let unclampedDelayTime = gifDict[kCGImagePropertyGIFUnclampedDelayTime as String] as? Double {
            return unclampedDelayTime
        } else if let delayTime = gifDict[kCGImagePropertyGIFDelayTime as String] as? Double {
            return delayTime
        }
        
        return nil
    }
    
    func setGif(_ gif: UIImage?) {
        self.image = gif
    }
    
    func startGif (){
        isAnimatingGif = true
        updateFrame()
    }
    
    func stopGif() {
        isAnimatingGif = false
    }
    
    func seekGif(to frame: Int)throws {
        if(frame < 0 || frame >= frameCount) {throw NSError(domain: "frame out of range", code: 0, userInfo: nil)}
        displayFrame(at: frame)
        index = frame
    }
    
    private func presetImage(_ ciImage: CIImage) {
        
    }
    
    func getPrimaryColor() {
    }
    
    private lazy var kernel: CIColorKernel? = {
        let bundleURL = Bundle.main.url(forResource: "GifControllerView", withExtension: "bundle")
        let bundle = Bundle(url: bundleURL!)
        if let resourcePath = bundle?.resourcePath {
            let documentsPath = resourcePath.appending("/Documents")
            print(documentsPath)
        }
        guard let url = bundle?.url(forResource: "default", withExtension: "metallib"),
              let data = try? Data(contentsOf: url)
        else {
            print("no data")
            return nil }
        let library = try! MTLCreateSystemDefaultDevice()!.makeLibrary(URL: url)
        print("functions:\(library.functionNames)")
        return try? CIColorKernel(functionName: "colorTransform_kernel",
                                  fromMetalLibraryData: data,
                                  outputPixelFormat: CIFormat.RGBAh)
    }()
    
    func changeColor(_ inputImage: CIImage) -> CIImage? {
        guard let kernel = kernel
            else { return inputImage }
        var outputImage = inputImage
        for(
            index,
            colorSet
        ) in fromToColorSet.enumerated() {
            
            let sampler = CISampler(image: outputImage)
             return kernel.apply(
                extent: outputImage.extent,
                roiCallback: { (_, _) in .null },
                arguments: [
                    sampler,
                    colorSet.from,
                    colorSet.to
                ])

        }
        return outputImage
    }

    struct ColorCount {
        let color: CIColor
        var count: Int
    }

    func getAllColorCount(_ frame: Int) -> [ColorCount] {
        image = UIImage(cgImage: CGImageSourceCreateImageAtIndex(gifSource!, frame, nil)!)
        if(image == nil) {return []}
        var colors: [ColorCount] = []
        for x in 0..<Int(image!.size.width) {
            for y in 0..<Int(image!.size.height) {
                let point = CGPoint(x: x, y: y)
                let color = getPixelColor(image!, point)
                if let index = colors.firstIndex(where: { $0.color == color }) {
                    colors[index].count += 1
                } else {
                    colors.append(ColorCount(color: color, count: 1))
                }
            }
        }
        colors.sort { $0.count > $1.count }
        return colors
    }
    
    func getPixelColor(_ image: UIImage, _ point: CGPoint) -> CIColor {
        let pixelData = image.cgImage!.dataProvider!.data
        let data: UnsafePointer<UInt8> = CFDataGetBytePtr(pixelData)
        let pixelInfo: Int = ((Int(image.size.width) * Int(point.y)) + Int(point.x)) * 4
       let r = CGFloat(data[pixelInfo]) / CGFloat(255.0)
       let g = CGFloat(data[pixelInfo + 1]) / CGFloat(255.0)
       let b = CGFloat(data[pixelInfo + 2]) / CGFloat(255.0)
        let a = CGFloat(data[pixelInfo + 3]) / CGFloat(255.0)
        return CIColor(red: r, green: g, blue: b, alpha: a)
    }
    
    @objc func setIsReverse(_ isReverse: Bool) {
        self.isReverse = isReverse
    }

    @objc func setSpeed(_ speed: NSNumber) {
      self.speed = 1 / speed.doubleValue
    }

    @objc func setIsAnimating(_ isAnimatingGif: Bool) {
        if(isAnimatingGif) {
        startGif()
      } else {
        stopGif()
      }
    }

    func getFrameData() -> [FrameData] {
        let frameDataList: [FrameData] = (0..<frameCount).map { (index) -> FrameData in
            let delayTime = getDelayTime(for: self.gifSource!, index: index)
            return FrameData(delayTime: delayTime ?? 1.0, frameIndex: index)
        }
        return frameDataList
    }
}

private final class BundleToken {}

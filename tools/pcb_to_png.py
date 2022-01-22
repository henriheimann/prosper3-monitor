import argparse
import os
import xml.etree.ElementTree as ET
import re
import time

try:
    import pcbnew
    from pcbnew import *
except ImportError as error:
    print("Unable to import PCBNew, are you using KiCAD included Python?: {0}".format(error))
    exit()

try:
    from wand.api import library
    import wand.color
    import wand.image
except ImportError as error:
    print("Unable to import Wand: {0}".format(error))
    exit()

greenStandard = {
    'Copper': ['#E8D959', 0.85],
    'CopperInner': ['#402400', 0.80],
    'SolderMask': ['#1D5D17', 0.80],
    'Paste': ['#9E9E9E', 0.95],
    'Silk': ['#eaebe5', 1.00],
    'Edge': ['#000000', 0.20],
    'BackGround': ['#998060']
}

blackStandard = {
    'Copper': ['#888888', 0.85],
    'SolderMask': ['#000000', 0.90],
    'Paste': ['#9E9E9E', 0.95],
    'Silk': ['#eaebe5', 1.00],
    'Edge': ['#000000', 0.20],
    'BackGround': ['#383838']
}

colours = blackStandard


def unique_prefix():
    unique_prefix.counter += 1
    return "pref_" + str(unique_prefix.counter)


unique_prefix.counter = 0


def ki2dmil(val):
    return val / 2540


def kiColour(val):
    return (val & 0xFF0000 >> 24) / 255


class svgObject(object):
    # Create a Blank SVG
    def __init__(self, pcb, mirror=False):
        self.bb = pcb.GetBoardEdgesBoundingBox()
        self.mirror = mirror
        self.et = ET.ElementTree(ET.fromstring("""<svg width="29.7002cm" height="21.0007cm" viewBox="0 0 116930 82680">
<title>Picutre generated by pcb2svg</title>
<desc>Picture generated by pcb2svg</desc>
<defs> </defs>
</svg>"""))
        self.svg = self.et.getroot()
        defs = self.svg.find('defs')

        newMask = ET.SubElement(defs, 'mask', id="boardMask",
                                width="{}".format(ki2dmil(self.bb.GetWidth())),
                                height="{}".format(ki2dmil(self.bb.GetHeight())),
                                x="{}".format(ki2dmil(self.bb.GetX())),
                                y="{}".format(ki2dmil(self.bb.GetY())))
        if self.mirror:
            newMask.attrib['transform'] = "scale(-1,1)"

        rect = ET.SubElement(newMask, 'rect',
                             width="{}".format(ki2dmil(self.bb.GetWidth())),
                             height="{}".format(ki2dmil(self.bb.GetHeight())),
                             x="{}".format(ki2dmil(self.bb.GetX())),
                             y="{}".format(ki2dmil(self.bb.GetY())),
                             style="fill:#FFFFFF; fill-opacity:1.0;")

    # Open an SVG file
    def openSVG(self, filename):
        prefix = unique_prefix() + "_"
        root = ET.parse(filename)

        # We have to ensure all Ids in SVG are unique. Let's make it nasty by
        # collecting all ids and doing search & replace
        # Potentially dangerous (can break user text)
        ids = []
        for el in root.iter():
            if "id" in el.attrib and el.attrib["id"] != "origin":
                ids.append(el.attrib["id"])
        with open(filename) as f:
            content = f.read()
        for i in ids:
            content = content.replace("#" + i, "#" + prefix + i)

        root = ET.fromstring(content)
        # Remove SVG namespace to ease our lifes and change ids
        for el in root.iter():
            if "id" in el.attrib and el.attrib["id"] != "origin":
                el.attrib["id"] = prefix + el.attrib["id"]
            if '}' in str(el.tag):
                el.tag = el.tag.split('}', 1)[1]
        self.svg = root

    # Wrap all image data into a group and return that group
    def extractImageAsGroup(self):
        wrapper = ET.Element('g',
                             width="{}".format(ki2dmil(self.bb.GetWidth())),
                             height="{}".format(ki2dmil(self.bb.GetHeight())),
                             x="{}".format(ki2dmil(self.bb.GetX())),
                             y="{}".format(ki2dmil(self.bb.GetY())),
                             style="fill:#000000; fill-opacity:1.0; stroke:#000000; stroke-opacity:1.0;")
        wrapper.extend(self.svg.iter('g'))
        return wrapper

    def reColour(self, transform_function):
        wrapper = self.extractImageAsGroup()
        # Set fill and stroke on all groups
        for group in wrapper.iter():
            svgObject._apply_transform(group, {
                'fill': transform_function,
                'stroke': transform_function,
            })
        self.svg = wrapper

    @staticmethod
    def _apply_transform(node, values):
        try:
            original_style = node.attrib['style']
            for (k, v) in values.items():
                escaped_key = re.escape(k)
                m = re.search(r'\b' + escaped_key + r':(?P<value>[^;]*);', original_style)
                if m:
                    transformed_value = v
                    original_style = re.sub(
                        r'\b' + escaped_key + r':[^;]*;',
                        k + ':' + transformed_value + ';',
                        original_style)
            node.attrib['style'] = original_style
        except Exception as e:
            style_string = " "
            node.attrib['style'] = style_string
            pass

    def addholes(self, holeData):
        self.svg.append(holeData)
        if self.mirror:
            holeData.attrib['transform'] = "scale(-1,1)"

    # holeData.attrib['mask'] =  "url(#boardMask);"

    def addSvgImageInvert(self, svgImage, colour):
        defs = self.svg.find('defs')
        newMask = ET.SubElement(defs, 'mask', id="test-a",
                                width="{}".format(ki2dmil(self.bb.GetWidth())),
                                height="{}".format(ki2dmil(self.bb.GetHeight())),
                                x="{}".format(ki2dmil(self.bb.GetX())),
                                y="{}".format(ki2dmil(self.bb.GetY())))
        if self.mirror:
            newMask.attrib['transform'] = "scale(-1,1)"

        rect = ET.SubElement(newMask, 'rect',
                             width="{}".format(ki2dmil(self.bb.GetWidth())),
                             height="{}".format(ki2dmil(self.bb.GetHeight())),
                             x="{}".format(ki2dmil(self.bb.GetX())),
                             y="{}".format(ki2dmil(self.bb.GetY())),
                             style="fill:#FFFFFF; fill-opacity:1.0;")

        imageGroup = svgImage.extractImageAsGroup()
        newMask.append(imageGroup)

        # create a rectangle to mask through
        wrapper = ET.SubElement(self.svg, 'g',
                                style="fill:{}; fill-opacity:0.75;".format(colour))
        rect = ET.SubElement(wrapper, 'rect',
                             width="{}".format(ki2dmil(self.bb.GetWidth())),
                             height="{}".format(ki2dmil(self.bb.GetHeight())),
                             x="{}".format(ki2dmil(self.bb.GetX())),
                             y="{}".format(ki2dmil(self.bb.GetY())))

        #wrapper.attrib['mask'] = "url(#test-a);"

        #if self.mirror:
        #    wrapper.attrib['transform'] = "scale(-1,1)"

    def addSvgImage(self, svgImage, colour, nofill=False):

        # create a rectangle to mask through
        wrapper = ET.SubElement(self.svg, 'g')

        imageGroup = svgImage.extractImageAsGroup()
        wrapper.append(imageGroup)

        for group in imageGroup.iter():
            svgObject._apply_transform(group, {
                'fill': colour,
                'stroke': colour,
            })
            if nofill:
                if 'stroke-width:0.000394;' not in group.attrib['style']:
                    svgObject._apply_transform(group, {
                        'fill-opacity': "0.0",
                    })
        if self.mirror:
            wrapper.attrib['transform'] = "scale(-1,1)"

    def write(self, filename):
        with open(filename, 'wb') as output_file:
            self.et.write(output_file)


def get_hole_mask(board):
    mask = ET.Element("g", id="hole-mask")
    container = ET.SubElement(mask, "g", style="opacity:0.9;")

    # Print all Drills
    for mod in board.GetModules():
        for pad in mod.Pads():
            pos = pad.GetPosition()
            pos_x = ki2dmil(pos.x)
            pos_y = ki2dmil(pos.y)
            size = ki2dmil(min(pad.GetDrillSize()))  # Tracks will fail with Get Drill Value

            length = 1
            if pad.GetDrillSize()[0] != pad.GetDrillSize()[1]:
                length = ki2dmil(max(pad.GetDrillSize()) - min(pad.GetDrillSize()))

            # length = 200
            stroke = size
            # print(str(size) + " " +  str(length) + " " + str(pad.GetOrientation()))

            points = "{} {} {} {}".format(0, -length / 2, 0, length / 2)
            if pad.GetDrillSize()[0] >= pad.GetDrillSize()[1]:
                points = "{} {} {} {}".format(length / 2, 0, -length / 2, 0)
            el = ET.SubElement(container, "polyline")
            el.attrib["stroke-linecap"] = "round"
            el.attrib["stroke"] = "black"
            el.attrib["stroke-width"] = str(stroke)
            el.attrib["points"] = points
            el.attrib["transform"] = "translate({} {})".format(
                pos_x, pos_y)
            el.attrib["transform"] += "rotate({})".format(
                -pad.GetOrientation() / 10)

    # Print all Vias
    for track in board.GetTracks():
        if track.GetClass() == "VIA":
            track = Cast_to_VIA(track)
            pos = track.GetPosition()
            pos_x = ki2dmil(pos.x)
            pos_y = ki2dmil(pos.y)
            size = ki2dmil(track.GetDrill())  # Tracks will fail with Get Drill Value

            stroke = size
            length = 1

            points = "{} {} {} {}".format(0, -length / 2, 0, length / 2)
            el = ET.SubElement(container, "polyline")
            el.attrib["stroke-linecap"] = "round"
            el.attrib["stroke"] = "black"
            el.attrib["opacity"] = "1.0"
            el.attrib["stroke-width"] = str(stroke)
            el.attrib["points"] = points
            el.attrib["transform"] = "translate({} {})".format(
                pos_x, pos_y)

    return mask


def plot_layer(layer_info, pctl):
    pctl.SetLayer(layer_info[0])
    pctl.OpenPlotfile("", PLOT_FORMAT_SVG, "")
    pctl.PlotLayer()
    time.sleep(0.01)
    pctl.ClosePlot()
    return pctl.GetPlotFileName()


def render(pcb_file_path, temp_dir, output_file_path, plot_plan, mirror=False):
    pcb = LoadBoard(pcb_file_path)

    bounding_box = pcb.GetBoardEdgesBoundingBox()

    pctl = PLOT_CONTROLLER(pcb)
    popt = pctl.GetPlotOptions()

    popt.SetPlotFrameRef(False)
    popt.SetAutoScale(False)
    popt.SetPlotViaOnMaskLayer(False)

    kicad_version = 5
    try:
        popt.SetLineWidth(FromMM(0.35))
    except:
        kicad_version = 6

    popt.SetAutoScale(False)
    popt.SetMirror(False)
    popt.SetUseGerberAttributes(False)
    popt.SetExcludeEdgeLayer(True)

    popt.SetScale(1)
    popt.SetUseAuxOrigin(False)
    if kicad_version == 6:
        popt.SetScale(1 / 2540)
        popt.SetUseAuxOrigin(True)

    popt.SetMirror(False)
    popt.SetUseGerberAttributes(False)
    popt.SetExcludeEdgeLayer(True)
    popt.SetNegative(False)
    popt.SetPlotReference(True)
    popt.SetPlotValue(True)
    popt.SetPlotInvisibleText(False)
    popt.SetDrillMarksType(PCB_PLOT_PARAMS.FULL_DRILL_SHAPE)
    pctl.SetColorMode(True)

    popt.SetOutputDirectory(temp_dir)
    popt.SetSubtractMaskFromSilk(False)

    canvas = svgObject(pcb, False)
    for layer_info in plot_plan:

        plot_layer(layer_info, pctl)

        svg_data = svgObject(pcb, False)
        svg_data.openSVG(pctl.GetPlotFileName())

        if layer_info[1] == "Invert":
            canvas.addSvgImageInvert(svg_data, colours[layer_info[2]][0])
        else:
            if layer_info[2] == 'Silk':
                canvas.addSvgImage(svg_data, colours[layer_info[2]][0], nofill=True)
            else:
                canvas.addSvgImage(svg_data, colours[layer_info[2]][0])

    # Drills are seperate from Board layers. Need to be handled differently
    canvas.addholes(get_hole_mask(pcb))

    output_file_name = os.path.basename(output_file_path)
    final_svg = os.path.join(temp_dir, output_file_name + '-merged.svg')
    canvas.write(final_svg)

    w = bounding_box.GetWidth() / 25400
    h = bounding_box.GetHeight() / 25400
    x = bounding_box.GetX() / 25400
    y = bounding_box.GetY() / 25400

    with wand.image.Image(filename=final_svg, resolution=1000, background=wand.color.Color(colours['BackGround'][0])) as image:
        image.crop(x, y, width=w, height=h)
        if mirror:
            image.flop()
        image.save(filename=output_file_path)


def main(pcb_file_path):
    pcb_file_dir = os.path.dirname(pcb_file_path)
    pcb_file_name = os.path.basename(pcb_file_path)

    output_dir = os.path.join(pcb_file_dir, 'plot')
    temp_dir = os.path.join(output_dir, 'temp')

    plot_plan = [
        (F_Cu, "", 'Copper'),
        (F_Mask, 'Invert', 'SolderMask'),
        (F_Paste, "", 'Paste'),
        (F_SilkS, "", 'Silk'),
        #(Edge_Cuts, "", 'Edge'),
    ]

    render(pcb_file_path, temp_dir, os.path.join(output_dir, pcb_file_name + '-front.png'), plot_plan)

    plot_plan = [
        (B_Cu, "", 'Copper'),
        (B_Mask, 'Invert', 'SolderMask'),
        (B_Paste, "", 'Paste'),
        (B_SilkS, "", 'Silk'),
        #(Edge_Cuts, "", 'Edge'),
    ]

    render(pcb_file_path, temp_dir, os.path.join(output_dir, pcb_file_name + '-back.png'), plot_plan, mirror=True)

    """svg_file = ""
    output_filename = ""

    with wand.image.Image() as image:
        with wand.color.Color('transparent') as background_color:
            library.MagickSetBackgroundColor(image.wand,
                                             background_color.resource)
        image.read(blob=svg_file.read(), format="svg")
        png_image = image.make_blob("png32")

    with open(output_filename, "wb") as out:
        out.write(png_image)"""


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("pcb_file_path", help="the .kicad_pcb to convert to png")
    args = parser.parse_args()
    main(args.pcb_file_path)
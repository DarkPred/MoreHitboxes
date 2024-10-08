(function() {
    var exportHitboxAction = new Action({
        id: "export_hitbox",
        name: "Export Hitboxes",
        icon: "flip_to_back",
        description: "Export a file containg the hitbox data",
        category: "file",
        click: function (event) {
            exportHitbox(undefined);
        },
    });
    var createHitboxAnimAction = new Action({
        id: "create_hitbox_anim",
        name: "Create Hitbox Animation",
        icon: "flip_to_back",
        description: "Create an Animation for the Hitbox",
        category: "file",
        click: function (event) {
            createHitboxAnim(undefined);
        },
    });
    var copyCubeToHitboxAction = new Action({
        id: "copy_cube_to_hitbox_action",
        name: "Copy Hitbox Cube to Bone",
        icon: "fa-copy",
        description: "Copy the selected cube to the matching hitbox bone ending in '_hitbox'",
        click: function (event) {
            copyCubeToHitbox(undefined);
        },
    });
	function copyCubeToHitbox() {
		if (Cube.selected.length != 1 || Cube.selected[0].parent.name !== "hitboxes") {
            new Dialog({
                id: 'more_hitboxes_dialog',
                title: 'Invalid selection',
                lines: ["You have to select a cube inside the bone named 'hitboxes'"]
            }).show();
			return;
		}
		let selected = Cube.selected[0];
		//Center pivot point action
		Toolbars.element_origin.children[3].click();
		let targetGroup = findGroup(selected.name + "_hitbox");
		if (typeof targetGroup === "undefined") {
            new Dialog({
                id: 'more_hitboxes_dialog',
                title: 'Missing target',
				lines: [`Make sure that a bone with the name ${selected.name}_hitbox exists`]
            }).show();
			return;
		}
		//Remove/Overwrite previous cube
		for (let i = targetGroup.children.length - 1; i >= 0; i--) {
			if (targetGroup.children[0].type === "cube") targetGroup.children[0].remove();
		}
		//Copy selected cube
		Clipbench.copy({shiftKey: false});
		targetGroup.select();
		//Paste into targetGroup
		Clipbench.paste({shiftKey: false});
		var xRot = 0;
		var group = Group.selected;
		while (group.parent !== "root") {
			xRot = xRot + group.parent.rotation[0];
			group = group.parent;
		}
		//New cube not yet available
		Blockbench.once("update_view", function(data) {
			//New cube x rotation inverse of all parents
			data.elements[0].rotation[0] = -xRot;
			data.elements[0].color = data.elements[0].parent.color;
			data.elements[0].visibility = true;
			Canvas.updateAll();
		});
	}
    function createHitboxAnim() {
		let animator = Timeline.selected_animator;
		if (animator === null || !animator.group.name.includes("_hitbox")) {
            new Dialog({
                id: 'more_hitboxes_dialog',
                title: 'Incorrect target',
				lines: ["Make sure to select a bone with _hitbox in its name"]
            }).show();
			return;
		}
		let group = animator.group;
		/*let parents = [];
		while (group != "root") {
			parents.unshift(group);
			group = group.parent;
		}*/
		for (let i = Timeline.keyframes.length - 1; i >= 0; i--) {
			if (Timeline.keyframes[i].animator.name === animator.name) {
				Timeline.keyframes[i].remove();
			}
		}
		if (group.children.length > 0) {
			animator.rotation_global = true;
			let rotation = group.children[0].rotation;
			animator.createKeyframe({x: rotation[0], y: rotation[1], z: rotation[2]}, 0, "rotation", true);
		}
	}
    
    function exportHitbox() {
        var output;
        var group = findGroup("hitboxes");
        var elements = [];
        for (var i = 0; i < group.children.length; i++) {
            var cube = group.children[i];
            let size = [cube.to[0] - cube.from[0], cube.to[1] - cube.from[1], cube.to[2] - cube.from[2]];
            //invert offset to match minecraft rotation system
            let offset = [-(cube.from[0] + size[0] / 2), cube.from[1], -(cube.from[2] + size[2] / 2)];
			let element  = {name: cube.name, pos: [offset[0], offset[1], offset[2]], width: size[0], height: size[1]};
			let ref = findGroup(cube.name + "_hitbox");
			if (typeof ref !== 'undefined') {
				element["ref"] = ref.name;
			}
            elements.push(element);
        }
		output = {elements: elements};

        Blockbench.export({
            type : 'Hitboxes File (.json)',
            extensions: ['json'],
            savetype: 'json',
            name: Project.geometry_name,
            content: autoStringify(output)
        });
    }
    
    function findGroup(name) {
        return Group.all.find((group) => group.name === name);
    }
    
    // var helpMenu;
	var onBedrockCompile;
    
    Plugin.register('more_hitboxes', {
        title: 'More Hitboxes',
        author: 'DarkPred',
        icon: 'fa-cubes',
        description: 'Allows creating and exporting Hitboxes',
        tags: ["Minecraft: Java Edition"],
        version: '2.0.0',
        variant: 'desktop',
    
        onload() {
			console.log("loading");
            MenuBar.addAction(exportHitboxAction, 'file.export');
			MenuBar.addAction(createHitboxAnimAction, 'animation');
			MenuBar.addAction(copyCubeToHitboxAction, 'tools')
			onBedrockCompile = Codecs.bedrock.on("compile", function(data) {
				let bones = data.model["minecraft:geometry"][0].bones;
				
				for (let i = bones.length - 1; i >= 0; i--) {
					if (bones[i].name == "hitboxes") {
						bones.splice(i, 1);
					} else if (bones[i].name.includes("hitbox")) {
						bones[i].cubes = []
					}
				}
			});
        },
        onunload() {
			onBedrockCompile.delete();
            exportHitboxAction.delete();
            createHitboxAnimAction.delete();
			copyCubeToHitboxAction.delete();
        },
        oninstall() {},
        onuninstall() {}
    });
})();
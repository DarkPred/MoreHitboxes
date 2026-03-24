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
    var createHitboxBoneAction = new Action({
        id: "create_hitbox_bone",
        name: "Create Hitbox Bone",
        icon: "flip_to_back",
        description: "Create an empty bone for the Hitbox",
        category: "file",
        click: function (event) {
            createHitboxBone(undefined);
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
	function createHitboxBone() {
		if (Cube.selected.length != 1 || Cube.selected[0].parent.name !== "hitboxes") {
            new Dialog({
                id: 'more_hitboxes_dialog',
                title: 'Invalid selection',
                lines: ["You have to select a cube inside the bone named 'hitboxes'"]
            }).show();
			return;
		}
		let selected = Cube.selected[0];
		let targetGroup = findGroup(selected.name);
		if (typeof targetGroup === "undefined") {
            new Dialog({
                id: 'more_hitboxes_dialog',
                title: 'Missing target',
				lines: [`Make sure that a bone with the name ${selected.name} exists`]
            }).show();
			return;
        
        }
		targetGroup.select();
        let cube_size = [selected.to[0] - selected.from[0], selected.to[1] - selected.from[1], selected.to[2] - selected.from[2]];
        let cube_pos = [selected.from[0] + cube_size[0] / 2, selected.from[1], selected.from[2] + cube_size[2] / 2];
        //Simulate add_group action
        //See BarItems.add_group.click();
		Undo.initEdit({outliner: true, groups: []});
		let lowest_selected = Outliner.selected.concat(Group.multi_selected).filter(n => !n.parent?.selected);
		var add_group = lowest_selected.find(s => s instanceof Group) || lowest_selected[0];
        var base_group = new Group({
            origin: cube_pos,
            name: selected.name + '_hitbox'
        })
        base_group.setColor(3); //red
        base_group.isOpen = true

        if (lowest_selected.length >= 2 && add_group) {
            base_group.sortInBefore(add_group, 1);
            lowest_selected.forEach((s) => {
                s.addTo(base_group);
            })
        } else {
            base_group.addTo(add_group);
        }

        base_group.init().select();

        //Move pivot point of new bone to bottom center of selected hitbox cube
        //See getSelectionCenter() and onPointerMove() and moveElementsInSpace()
        let actual_pos = THREE.fastWorldPosition(base_group.mesh, new THREE.Vector3());
        let target_point = new THREE.Vector3(cube_pos[0], cube_pos[1], cube_pos[2]);
        let difference = new THREE.Vector3().add(target_point).sub(actual_pos);
    
        let rotation = new THREE.Quaternion();
        base_group.mesh.parent.getWorldQuaternion(rotation);
        difference.applyQuaternion(rotation.invert());

        //Apply in global space
        base_group.origin.V3_add(difference.x, difference.y, difference.z);
        Canvas.updateAllBones([base_group]);


        Undo.finishEdit('Add group', {outliner: true, groups: [base_group]});
        Vue.nextTick(function() {
            updateSelection();
            if (settings.create_rename.value) {
                base_group.rename();
            }
            Blockbench.dispatchEvent('add_group', { object: base_group });
        });
	}
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
        var cube_mesh = selected.getMesh();
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
		var group = Group.selected[0];
		while (group.parent !== "root") {
			xRot = xRot + group.parent.rotation[0];
			group = group.parent;
		}
		//New cube not yet available
        Blockbench.once("update_view", function (data) {
            let el = data.elements[0];
			//New cube x rotation inverse of all parents
			el.rotation[0] = -xRot;
			el.color = el.parent.color;
            el.visibility = true;
        
            //Move new cube to exact global position of selected hitbox cube
            //TODO: Still somewhat inaccurate with rotations
            let actual_pos = THREE.fastWorldPosition(el.mesh, new THREE.Vector3());
            let target_point = THREE.fastWorldPosition(cube_mesh, new THREE.Vector3());
            let difference = new THREE.Vector3().add(target_point).sub(actual_pos);
            
            let rotation = new THREE.Quaternion();
            el.mesh.getWorldQuaternion(rotation);
            difference.applyQuaternion(rotation.invert());

            el.from.V3_add(difference.x, difference.y, difference.z);
            el.to.V3_add(difference.x, difference.y, difference.z);
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
        if (group !== undefined) {
            for (var i = 0; i < group.children.length; i++) {
                var cube = group.children[i];
                let size = [cube.to[0] - cube.from[0], cube.to[1] - cube.from[1], cube.to[2] - cube.from[2]];
                //invert offset to match minecraft rotation system
                let offset = [-(cube.from[0] + size[0] / 2), cube.from[1], -(cube.from[2] + size[2] / 2)];
                let element  = {name: cube.name, pos: [offset[0], offset[1], offset[2]], width: size[0], height: size[1]};
                let ref = findGroup(cube.name + "_hitbox");
                if (ref !== undefined) {
                    element["ref"] = ref.name;
                }
                elements.push(element);
            }
        }
        var addAnchor = (group) => {
            if (group !== undefined) {
                elements.push({name: group.name, pos: [-group.origin[0], group.origin[1], -group.origin[2]], ref: group.name, is_anchor: true});
            }
        }
        addAnchor(findGroup("rider_pos"));
        addAnchor(findGroup("grab_pos"));
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
        version: '2.4.0',
        variant: 'desktop',
    
        onload() {
			console.log("loading");
            MenuBar.addAction(exportHitboxAction, 'file.export');
			MenuBar.addAction(createHitboxAnimAction, 'animation');
			MenuBar.addAction(createHitboxBoneAction, 'tools')
			MenuBar.addAction(copyCubeToHitboxAction, 'tools')
			onBedrockCompile = Codecs.bedrock.on("compile", function(data) {
				let bones = data.model["minecraft:geometry"][0].bones;
				
                for (let i = bones.length - 1; i >= 0; i--) {
                    if (bones[i] == undefined) {
                        //Export manually disabled
                    } else if (bones[i].name == "hitboxes") {
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
			createHitboxBoneAction.delete();
			copyCubeToHitboxAction.delete();
        },
        oninstall() {},
        onuninstall() {}
    });
})();
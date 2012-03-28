package org.craftmania.blocks.customblocks;

import static org.craftmania.rendering.ChunkMeshBuilder.*;

import java.nio.FloatBuffer;

import org.craftmania.Side;
import org.craftmania.blocks.Block;
import org.craftmania.blocks.BlockManager;
import org.craftmania.blocks.BlockType;
import org.craftmania.datastructures.AABB;
import org.craftmania.inventory.InventoryItem;
import org.craftmania.math.Vec2f;
import org.craftmania.math.Vec3f;
import org.craftmania.math.Vec3i;
import org.craftmania.world.Chunk;
import org.craftmania.world.LightBuffer;

public class Redstone extends Block implements RedstoneLogic
{

	private static final float MINIMUM_TEXTURE_SIZE = 3.0f / 16.0f / 16.0f;
	private static final float MAXIMUM_TEXTURE_SIZE = 8.0f / 16.0f / 16.0f;
	private static final float MINIMUM_TERRAIN_SIZE = 3.0f / 16.0f;
	private static final Vec2f TEXTURE_CENTER_CROSS;
	private static final Vec2f TEXTURE_CENTER_LINE;

	static
	{
		TEXTURE_CENTER_CROSS = new Vec2f(4.5f, 10.5f);
		TEXTURE_CENTER_LINE = new Vec2f(5.5f, 10.5f);

		TEXTURE_CENTER_CROSS.scale(16.0f / 256.0f);
		TEXTURE_CENTER_LINE.scale(16.0f / 256.0f);
	}

	private int _connectionCount;
	private boolean _powered;
	private boolean[] _connections;
	private boolean _visible;

	public Redstone(Chunk chunk, Vec3i pos)
	{
		super(BlockManager.getInstance().getBlockType("redstone"), chunk, pos);

		_connections = new boolean[6];

		_aabb = new AABB(new Vec3f(getPosition()).add(0.5f, 0.02f, 0.5f), new Vec3f(0.5f, 0.02f, 0.5f));
	}

	@Override
	public void feed()
	{
		if (!_powered)
		{
			_powered = true;
			/* Conduct the power */
			Vec3i n;
			for (int i = 0, j = 0; i < 6 && j < _connectionCount; ++i)
			{
				if (_connections[i])
				{
					++j;
					n = Side.getSide(i).getNormal();
					RedstoneLogic rl = (RedstoneLogic) _chunk.getSpecialBlockAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z());
					rl.feed();
				}
			}
		}
	}

	@Override
	public void unfeed()
	{
		if (_powered)
		{
			_powered = false;
			/* Conduct the power */
			Vec3i n;
			for (int i = 0, j = 0; i < 6 && j < _connectionCount; ++i)
			{
				if (_connections[i])
				{
					++j;
					n = Side.getSide(i).getNormal();
					RedstoneLogic rl = (RedstoneLogic) _chunk.getSpecialBlockAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z());
					rl.unfeed();
				}
			}
		}
	}

	@Override
	public boolean isPowered()
	{
		return _powered;
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void render(LightBuffer lightBuffer)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void storeInVBO(FloatBuffer vbo, LightBuffer lightBuffer)
	{
		boolean connectedL = _connections[Side.LEFT.ordinal()];
		boolean connectedR = _connections[Side.RIGHT.ordinal()];
		boolean connectedF = _connections[Side.FRONT.ordinal()];
		boolean connectedB = _connections[Side.BACK.ordinal()];

		float x = getX() + 0.5f;
		float y = getY() + 0.02f;
		float z = getZ() + 0.5f;
		Vec3f v = new Vec3f(1, 1, 1);
		if (!_powered)
		{
			v.scale(0.5f);
		}
		System.out.printf("Store Redstone (%b, %b, %b, %b)%n", connectedL, connectedR, connectedF, connectedB);
		if ((_connectionCount == 1 && (connectedL || connectedR)) || (_connectionCount == 2 && (connectedL && connectedR)))
		{
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() + MAXIMUM_TEXTURE_SIZE);
		} else if ((_connectionCount == 1 && (connectedF || connectedB)) || (_connectionCount == 2 && (connectedF && connectedB)))
		{
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_LINE.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_LINE.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 2 && (connectedL && connectedB))
		{
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 2 && (connectedR && connectedB))
		{
			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 2 && (connectedR && connectedF))
		{
			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 2 && (connectedL && connectedF))
		{
			put3f(vbo, x - 0.5f, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 3 && (connectedR && connectedL && connectedB))
		{
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MINIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 3 && (connectedR && connectedL && connectedF))
		{
			put3f(vbo, x - 0.5f, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - MINIMUM_TERRAIN_SIZE);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MINIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 3 && (connectedR && connectedF && connectedB))
		{
			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - MINIMUM_TERRAIN_SIZE, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 3 && (connectedL && connectedF && connectedB))
		{
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + MINIMUM_TERRAIN_SIZE, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MINIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		} else if (_connectionCount == 4 || _connectionCount == 0)
		{
			put3f(vbo, x - 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 0), lightBuffer.get(1, 1, 0), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z - 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(1, 1, 0), lightBuffer.get(2, 1, 0), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() - MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x + 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(2, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(2, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() + MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);

			put3f(vbo, x - 0.5f, y, z + 0.5f);
			putColorWithLight4(vbo, v, lightBuffer.get(1, 1, 1), lightBuffer.get(0, 1, 2), lightBuffer.get(1, 1, 2), lightBuffer.get(0, 1, 1));
			put2f(vbo, TEXTURE_CENTER_CROSS.x() - MAXIMUM_TEXTURE_SIZE, TEXTURE_CENTER_CROSS.y() + MAXIMUM_TEXTURE_SIZE);
		}

	}

	@Override
	public boolean isVisible()
	{
		return _visible;
	}

	@Override
	public AABB getAABB()
	{
		return _aabb;
	}

	@Override
	public void smash(InventoryItem item)
	{
		destory();
	}

	@Override
	public void neighborChanged(Side side)
	{
		Vec3i n = side.getNormal();
		byte bType = _chunk.getBlockTypeAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z(), false, false, false);
		if (bType <= 0)
		{
			disconnect(side);
			return;
		}
		BlockType type = BlockManager.getInstance().getBlockType(bType);
		if (type.hasRedstoneLogic())
		{
			Block block = _chunk.getSpecialBlockAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z());
			RedstoneLogic rl = (RedstoneLogic) block;
			rl.connect(Side.getOppositeSide(side));
			connect(side);
		} else
		{
			disconnect(side);
		}
	}

	@Override
	public void checkVisibility()
	{
		_visible = true;
	}

	@Override
	public int getVertexCount()
	{
		return 4;
	}

	@Override
	public void connect(Side side)
	{
		if (!_connections[side.ordinal()])
		{
			_connectionCount++;
		}
		_connections[side.ordinal()] = true;
		if (isPowered())
		{
			feedNeighbor(side);
		}
	}

	@Override
	public void disconnect(Side side)
	{
		if (_connections[side.ordinal()])
		{
			_connectionCount--;
		}
		_connections[side.ordinal()] = false;
	}

	private void feedNeighbor(Side side)
	{
		Vec3i n = side.getNormal();
		RedstoneLogic rl = (RedstoneLogic) _chunk.getSpecialBlockAbsolute(getX() + n.x(), getY() + n.y(), getZ() + n.z());
		rl.feed();
	}
}
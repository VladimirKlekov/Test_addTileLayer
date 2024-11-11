package com.vk.testaddtilelayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vk.testaddtilelayer.databinding.FragmentMainBinding
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.images.DefaultImageUrlProvider
import com.yandex.mapkit.layers.Layer
import com.yandex.mapkit.layers.LayerOptions
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.layers.TileFormat
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CreateTileDataSource
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.tiles.UrlProvider
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var newLayer: Layer
    private lateinit var urlTileProvider: UrlProvider
    private var imageUrlProvider: DefaultImageUrlProvider? = null

    private var mapView: MapView? = null
    private lateinit var userLocation: UserLocationLayer
    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) = Unit
        override fun onObjectRemoved(view: UserLocationView) = Unit
        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocation.cameraPosition()?.target?.let {
                mapView?.map?.move(CameraPosition(it, 15F, 0F, 0F))
            }
            userLocation.setObjectListener(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        getMyLocation()
        imageUrlProvider = DefaultImageUrlProvider();
        urlTileProvider = UrlProvider { tileId, _, _ ->
            "https://core-sat.maps.yandex.net/tiles?l=sat&x=5004&y=2421&z=13&scale=1&lang=ru_RU" + tileId.z + "/" + tileId.x + "/" + tileId.y + ".jpg"
        }

        newLayer = binding.mapView.mapWindow.map.addTileLayer(
            "Test",
            LayerOptions().setVersionSupport(false),
            CreateTileDataSource { tileDataSourceBuilder ->
                tileDataSourceBuilder.setTileFormat(TileFormat.JPG)
                tileDataSourceBuilder.setTileUrlProvider(urlTileProvider)
            })


        binding.osm.setOnClickListener {
            binding.mapView.mapWindow.map.mapType = MapType.NONE
            newLayer.dataSourceLayer().isActive = true
        }

        binding.yandex.setOnClickListener {
            newLayer.dataSourceLayer().isActive = false
            binding.mapView.mapWindow.map.mapType = MapType.MAP
        }




        return view
    }




    private fun getMyLocation() {
        mapView = binding.mapView
        var mapKit: MapKit = MapKitFactory.getInstance()
        userLocation = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocation.isVisible = true
        userLocation.isHeadingEnabled = false
        userLocation.setObjectListener(locationObjectListener)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mapView = null
    }
}